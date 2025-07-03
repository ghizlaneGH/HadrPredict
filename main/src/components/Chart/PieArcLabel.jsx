import * as React from "react";
import { PieChart, pieArcLabelClasses } from "@mui/x-charts/PieChart";
import { bluePalette } from "@mui/x-charts";
import { fetchStats, valueFormatter } from "./webUsageStats";

export default function PieArcLabel() {
  const [data, setData] = React.useState([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    const loadData = async () => {
      const stats = await fetchStats();
      setData(stats);
      setLoading(false);
    };

    loadData();
  }, []);

  if (loading) {
    return <div>Chargement en cours...</div>;
  }
  console.log("Données formatées:", data);

  return (
    <PieChart
      colors={bluePalette}
      series={[
        {
          arcLabel: (item) => `${item.value}%`,
          arcLabelMinAngle: 35,
          arcLabelRadius: "60%",
          data: data,
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
