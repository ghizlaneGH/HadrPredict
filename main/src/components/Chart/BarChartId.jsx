// BarChartId.jsx
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

export default function BarChartId({ data }) {
  if (!data || data.length === 0) {
    return <p>Aucune donnée disponible</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="genre" />
        <YAxis unit="%" domain={[0, 100]} />
        <Tooltip
          formatter={(value) => [`${value.toFixed(2)}%`, "Pourcentage"]}
        />
        <Legend />
        <Bar dataKey="pourcent" fill="#8DD8FF" name="Prediction=(%)" />
      </BarChart>
    </ResponsiveContainer>
  );
}