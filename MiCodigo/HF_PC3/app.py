from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import cv2
import numpy as np
import urllib.request
from PIL import Image
from io import BytesIO

app = FastAPI()

# Modelo de entrada
class ImageURL(BaseModel):
    url: str

# Función para buscar rostros en la imagen
def buscar_existe(image):
    existe = "NO"
    face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5, minSize=(10, 10))
    if len(faces) > 0:
        existe = "SI"
    return existe

# Ruta de predicción
@app.post('/predict/')
async def predict(image_data: ImageURL):
    try:
        # Descargar la imagen desde la URL proporcionada
        response = urllib.request.urlopen(image_data.url)
        image = Image.open(BytesIO(response.read()))
        image = np.asarray(image)

        # Verificar si la imagen es válida
        if len(image.shape) < 2:
            raise ValueError("La URL no contiene una imagen válida")

        # Detección de rostros
        prediction = buscar_existe(image)

        return {"prediction": prediction}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error procesando la imagen: {str(e)}")
