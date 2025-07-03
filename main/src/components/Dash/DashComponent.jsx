import React, { useState, useEffect } from "react";
import styles from "./Dash.module.css";
import Sidebar from "../Layout/Sidebar/Sidebar";
import { useParams } from "react-router-dom";

import BarChartId from "../Chart/BarChartId";
import PieArcLabelID from "../Chart/PieArcLabelID";

const DashComponent = () => {
  const { schoolId } = useParams();
  const [schoolName, setSchoolName] = useState("");
  const [pieData, setPieData] = useState([]);
  const [barData, setBarData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cycle, setCycle] = useState("");

  useEffect(() => {
    if (schoolId) {
      fetch(`/api/schools/${schoolId}`)
        .then((res) => res.json())
        .then((data) => {
          setSchoolName(data.schoolName || `École ID ${schoolId}`);
          setPieData(data.pieData || []);
          setBarData(data.barData || []);
          setCycle(data.cycle || "Non disponible");
          setLoading(false);
        })
        .catch((err) => {
          console.error("Erreur lors du chargement ", err);
        });
    }
  }, [schoolId]);

  if (loading) return <p>Chargemnt du dashboard</p>;

  return (
    <div className="page-layout">
      <Sidebar />

      <main className={styles.mainContainer}>
        {/* Titre */}
        <div className={styles.mainTitle}>
          <h3>
            {schoolId ? `DASHBOARD - ${schoolName}` : "TABLEAU DE BORD GLOBAL"}
          </h3>
        </div>

        {/* Cartes */}
        <div className={styles.mainCards}>
          <div className={styles.card} style={{ backgroundColor: "#2962ff" }}>
            <div className={styles.cardInner}>
              <h3>Etablissement</h3>
            </div>
            <h1>{schoolId}</h1>
          </div>
          <div className={styles.card} style={{ backgroundColor: "#ff6d00" }}>
            <div className={styles.cardInner}>
              <h3>Cycle</h3>
            </div>
            <h1>{cycle}</h1>
          </div>
          <div className={styles.card} style={{ backgroundColor: "#2e7d32" }}>
            <div className={styles.cardInner}>
              <h3>ALERTS</h3>
            </div>
            <h1>{schoolId ? "5" : "42"}</h1>
          </div>
        </div>
        {/* Graphiques en ligne */}
        <div className={styles.chartsContainer}>
          <div className={styles.chartWrapper}>
            <h4 style={{ textAlign: "center", marginBottom: "10px" }}>
              Repartition des predictions
            </h4>
            <PieArcLabelID data={pieData} />
          </div>
          <div className={styles.chartWrapper}>
            <h4 style={{ textAlign: "center", marginBottom: "10px" }}>
              Predictions par genre
            </h4>
            <BarChartId data={barData} />
          </div>
        </div>
      </main>
    </div>
  );
};

export default DashComponent;
