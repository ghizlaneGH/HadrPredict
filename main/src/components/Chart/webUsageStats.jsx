export const fetchStats = async () => {
  try {
    const response = await fetch(
      "http://localhost:8081/api/eleve/par-prediction"
    );
    const stats = await response.json();

    // Conversion en format compatible avec MUI PieChart
    const formattedData = Object.entries(stats).map(([key, value]) => ({
      id: key,
      value: parseFloat(value),
    }));

    console.log("Données formatées:", formattedData);
    return formattedData;
  } catch (error) {
    console.error("Erreur lors du chargement des stats :", error);
    return [];
  }
};

export const valueFormatter = (item) => `${item.value}%`;
