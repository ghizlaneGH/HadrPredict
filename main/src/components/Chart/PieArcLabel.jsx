import * as React from "react";
import { PieChart, pieArcLabelClasses } from "@mui/x-charts/PieChart";

// Fonction pour formater les valeurs en pourcentage
const valueFormatter = (item) => `${item.value}%`;

// Fonction pour calculer les pourcentages et attribuer les couleurs
const calculatePercentages = (data) => {
  const total = data.reduce((sum, item) => sum + item.value, 0);
  return data.map((item) => {
    const color = item.id === "Abandonner" ? "#4682B4" : "#87CEEB";
    return {
      ...item,
      value: parseFloat(((item.value / total) * 100).toFixed(2)),
      color,
    };
  });
};

// Légende statique
const legendItems = [
  { color: "#87CEEB", label: "Exercer ces études" },
  { color: "#4682B4", label: "Abandonner" },
];

// Fonction simulée pour récupérer des données
const fetchStats = async () => {
  const response = await fetch("http://localhost:8081/api/eleve/par-prediction");

  if (!response.ok) {
    throw new Error("Erreur lors du chargement des statistiques");
  }

  const data = await response.json();

  // Conversion de l'objet en tableau [{ id, value }]
  return Object.entries(data).map(([key, value]) => ({
    id: key,
    value: value,
  }));
};


export default function PieArcLabelID({ data = [] }) {
  const [internalData, setInternalData] = React.useState([]);
  const [loading, setLoading] = React.useState(data.length === 0);

  React.useEffect(() => {
    const loadData = async () => {
      try {
        const stats = await fetchStats();
        const formattedData = calculatePercentages(stats);
        setInternalData(formattedData);
      } catch (error) {
        console.error("Erreur lors du chargement des données :", error);
      } finally {
        setLoading(false);
      }
    };

    if (data.length === 0) {
      loadData();
    } else {
      const formattedData = calculatePercentages(data);
      setInternalData(formattedData);
      setLoading(false);
    }
  }, [data]);

  if (loading) {
    return <div>Chargement...</div>;
  }

  return (
    <div style={{ display: "flex", alignItems: "center" }}>
      {/* Pie Chart */}
      <div style={{ marginRight: 30 }}>
        <PieChart
          series={[
            {
              arcLabel: (item) => `${item.value}%`,
              arcLabelMinAngle: 35,
              arcLabelRadius: "60%",
              data: internalData,
              valueFormatter,
              colorKey: "color", // Utiliser la clé "color" dans les données
            },
          ]}
          sx={{
            [`& .${pieArcLabelClasses.root}`]: {
              fontWeight: "bold",
            },
          }}
          width={400}
          height={300}
        />
      </div>

      {/* Légende */}
      <div>
        <ul style={{ listStyleType: "none", padding: 0, color: "black" }}>
          {legendItems.map((item, index) => (
            <li
              key={index}
              style={{
                display: "flex",
                alignItems: "center",
                marginBottom: 8,
                color: "black",
              }}
            >
              <div
                style={{
                  width: 20,
                  height: 20,
                  backgroundColor: item.color,
                  borderRadius: 4,
                  marginRight: 10,
                }}
              />
              <span>{item.label}</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
