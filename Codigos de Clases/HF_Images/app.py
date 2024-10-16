from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from typing import List
import cv2
from PIL import Image
import numpy as np
from io import BytesIO

app = FastAPI()
    
def buscar_existe(image):
    existe = "no"
    print("resultado: ",image.shape)
    face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3,5,minSize=(10, 10))
    for (x,y,w,h) in faces:
        existe = "si"
        break
    
    return existe
  
# Ruta de predicción
@app.post('/predict/')
async def predict(file: UploadFile = File(...)):
    try:
        image = Image.open(BytesIO(await file.read()))
        image = np.asarray(image)
        #if image.shape != (256, 256,3):
        #    raise ValueError("La imagen debe ser de tamaño 256x256.")
        prediction = buscar_existe(image)        
        return {"prediction": prediction}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))