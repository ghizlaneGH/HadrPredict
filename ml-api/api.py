from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import pandas as pd

# Charger le modèle entraîné
model = joblib.load("modele_abandon.pkl")

app = FastAPI()

# Classe d'entrée : mêmes colonnes que celles utilisées pour entraîner le modèle
class InputData(BaseModel):
    Resultat: float
    Absence: int
    Genre: str
    Milieu: str
    Cycle: str
    Type_etablissement: str
    age: int

@app.post("/predict")
def predict(data: InputData):
    # Créer un DataFrame avec une seule ligne
    input_df = pd.DataFrame([data.dict()])
    
    # Prédiction
    prediction = model.predict(input_df)[0]
    proba = model.predict_proba(input_df)[0][1]

    return {
        "prediction": int(prediction),
        "probability_abandon": round(proba, 3)
    }
