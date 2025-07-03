import * as React from "react";
import { PieChart, pieArcLabelClasses } from "@mui/x-charts/PieChart";
import { bluePalette } from "@mui/x-charts";

// Fonction pour formater les valeurs en pourcentage
const valueFormatter = (item) => `${item.value}%`;

// Fonction pour calculer les pourcentages
const calculatePercentages = (data) => {
  const total = data.reduce((sum, item) => sum + item.value, 0);
  return data.map((item) => ({
    ...item,
    value: parseFloat(((item.value / total) * 100).toFixed(2)),
  }));
};

export default function PieArcLabelID({ data = [] }) {
  const [internalData, setInternalData] = React.useState([]);
  const [loading, setLoading] = React.useState(data.length === 0);

  React.useEffect(() => {
    const loadData = async () => {
      const stats = await fetchStats();
      const formattedData = calculatePercentages(stats);
      setInternalData(formattedData);
      setLoading(false);
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
    <PieChart
      colors={bluePalette}
      series={[
        {
          arcLabel: (item) => `${item.value}%`,
          arcLabelMinAngle: 35,
          arcLabelRadius: "60%",
          data: internalData,
          valueFormatter,
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
  );
}
