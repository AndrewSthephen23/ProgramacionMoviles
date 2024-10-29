from keras.api.models import Sequential
from keras.api.layers import InputLayer, Dense
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import numpy as np
from typing import List


class InputData(BaseModel):
    data: List[float]  # Lista de características numéricas (flotantes)


app = FastAPI()


# Función para construir el modelo manualmente
def build_model():
    model = Sequential(
        [
            InputLayer(
                input_shape=(2,), name="dense_2_input"
            ),  # Ajusta el tamaño de entrada según tu modelo
            Dense(16, activation="relu", name="dense_2"),
            Dense(1, activation="sigmoid", name="dense_3"),
        ]
    )
    model.load_weights(
        "model.h5"
    )  # Asegúrate de que los nombres de las capas coincidan para que los pesos se carguen correctamente
    model.compile(
        loss="mean_squared_error", optimizer="adam", metrics=["binary_accuracy"]
    )
    return model


model = build_model()  # Construir el modelo al iniciar la aplicación


# Ruta de predicción
@app.post("/predict/")
async def predict(data: InputData):
    print(f"Data: {data}")
    global model
    try:
        # Convertir la lista de entrada a un array de NumPy para la predicción
        input_data = np.array(data.data).reshape(
            1, -1
        )  # Asumiendo que la entrada debe ser de forma (1, num_features)
        prediction = model.predict(input_data).round()
        return {"prediction": prediction.tolist()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
