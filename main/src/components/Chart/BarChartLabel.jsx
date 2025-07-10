import React, { useEffect, useState } from "react";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from "recharts";

const BarChartLabel = () => {
  const [chartData, setChartData] = useState([
    { name: "Filles", abandon: 0 },
    { name: "Garçons", abandon: 0 },
  ]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(
          "http://localhost:8081/api/eleve/pred-par-genre"
        );

        if (!response.ok) {
          throw new Error(`Erreur HTTP : ${response.status}`);
        }

        const data = await response.json();

        const updatedData = [
          {
            name: "Filles",
            abandon: data.Filles || 0,
          },
          {
            name: "Garçons",
            abandon: data.Garçons || 0,
          },
        ];

        setChartData(updatedData);
        setError(null);
      } catch (err) {
        console.error("Erreur lors du chargement:", err);
        setError("Impossible de charger les donnees");
        setChartData([
          { name: "Filles", abandon: 0 },
          { name: "Garçons", abandon: 0 },
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <div>Chargement des données en cours...</div>;

  if (error) return <div style={{ color: "red" }}>{error}</div>;

  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={chartData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" />
        <YAxis unit="%" domain={[0, 100]} />
        <Tooltip
          formatter={(value) => [`${value.toFixed(2)}%`, "Pourcentage"]}
        />
        <Legend />
        <Bar dataKey="abandon" fill="#8DD8FF" />
      </BarChart>
    </ResponsiveContainer>
  );
};

export default BarChartLabel;