import React from "react";

// Fonction pour récupérer les stats depuis l'API
export const fetchStats = async () => {
  try {
    const response = await fetch(
      "http://localhost:8081/api/eleve/par-prediction"
    );
    const stats = await response.json();
    console.log("Données brutes:", stats);

    const formattedData = Object.entries(stats).map(([label, value]) => ({
      label,
      value: parseFloat(value).toFixed(2),
    }));

    return formattedData;
  } catch (error) {
    console.error("Erreur lors du chargement des stats :", error);
    return [];
  }
};

export const valueFormatter = (item) => `${item.value}%`;
