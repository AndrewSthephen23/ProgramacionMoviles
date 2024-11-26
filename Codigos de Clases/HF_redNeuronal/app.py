import numpy as np
np.random.seed(0)
import pickle
import requests
from minisom import MiniSom
import cv2
import urllib.request
import uuid
from scipy.ndimage import median_filter
from scipy.signal import convolve2d
from io import BytesIO
from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from typing import List


class InputData(BaseModel):
    data: str
    
# Inicializar la aplicación FastAPI
app = FastAPI()

def load_model():
    with open('somhuella.pkl', 'rb') as fid:
        somhuella = pickle.load(fid)
    MM = np.loadtxt('matrizMM.txt', delimiter=" ")
    return somhuella,MM



def sobel(patron):
  gx = np.array([[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]], dtype=np.float32)
  gy = np.array([[1, 2, 1], [0, 0, 0], [-1, -2, -1]], dtype=np.float32)

  Gx = convolve2d(patron, gx, mode='valid')
  Gy = convolve2d(patron, gy, mode='valid')

  return Gx, Gy

def medfilt2(G, d=3):
  return median_filter(G, size=d)

def orientacion(patron, w):
  Gx, Gy = sobel(patron)
  Gx = medfilt2(Gx)
  Gy = medfilt2(Gy)

  m, n = Gx.shape
  mOrientaciones = np.zeros((m // w, n // w), dtype=np.float32)

  for i in range(m // w):
    for j in range(n // w):
      Gx_patch = Gx[i*w:(i+1)*w, j*w:(j+1)*w]
      Gy_patch = Gy[i*w:(i+1)*w, j*w:(j+1)*w]

      YY = np.sum(2 * Gx_patch * Gy_patch)
      XX = np.sum(Gx_patch**2 - Gy_patch**2)

      mOrientaciones[i, j] = (0.5 * np.arctan2(YY, XX) + np.pi / 2.0) * (18.0 / np.pi)

  return mOrientaciones
    
def redimensionar(img, h, v):
  return cv2.resize(img, (h, v), interpolation=cv2.INTER_AREA)
    
def representativo(imarray):
    imarray = np.squeeze(imarray)
    m, n = imarray.shape
    patron = imarray[1:m-1, 1:n-1]
    EE = orientacion(patron, 14)
    return np.asarray(EE).reshape(-1)

somhuella,MM = load_model()
# Ruta de predicción
@app.post("/predict/")
async def predict(data: InputData):
    print(f"Data: {data}")
    global somhuella
    global MM
    try:
        
        miUrl = data.data
        archivo = f"/tmp/test-{uuid.uuid4()}.tif"
        urllib.request.urlretrieve(miUrl, archivo)
        Xtest = redimensionar(cv2.imread(archivo),256,256)
        Xtest = np.array(Xtest)
        Xtest = cv2.cvtColor(Xtest, cv2.COLOR_BGR2GRAY)
        orientaciones = orientacion(Xtest, w=14)
        
        #Xtest = Xtest.astype('float32') / 255.0
        
        orientaciones = orientaciones.reshape(-1)
        
        w = somhuella.winner(orientaciones)
        
        
        print(miUrl)
        
        
        #response = requests.get(miUrl)
        """
        img = Image.open(BytesIO(response.content))
        image = np.asarray(img)
        image = image.reshape(256, 256, 1)
        representative_data = representativo(image)
        representative_data = representative_data.reshape(1, -1)
        
        w = somhuella.winner(representative_data)
        print(f"indice ganador del SOM: {w}")
        prediction = MM[w]
        """
        prediction = MM[w]
        print(f"Predicción: {prediction}")
        return {"prediction": prediction}
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))