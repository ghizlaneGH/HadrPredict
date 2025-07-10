import React, { useEffect, useState } from "react";
import styles from "./Dash.module.css";
import Sidebar from "../Layout/Sidebar/Sidebar";
import "flowbite";
import {
  BsFillGrid3X3GapFill,
  BsPeopleFill,
  BsFillBellFill,
} from "react-icons/bs";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import PieArcLabel from "../Chart/PieArcLabel";
import EtablissementsComponent from "./EtablissementsComponent";
import BarChartLabel from "../Chart/BarChartLabel";

function DashGcomponent() {
  const [filters, setFilters] = useState({
    niveau: "",
    genre: "",
    milieu: "",
    type: "",
  });

  const [count, setCount] = useState(0);
  const [showEtablissements, setShowEtablissements] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      if (window.Flowbite && window.Flowbite.initDropdowns) {
        window.Flowbite.initDropdowns();
      }
    }, 500);

    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    const fetchCount = async () => {
      try {
        const response = await fetch("/api/schools/count");
        if (!response.ok)
          throw new Error(
            "Erreur lors de la recuperation du nombre des ecoles"
          );
        const data = await response.json();
        setCount(data);
      } catch (error) {
        console.error("Erreur", error);
      }
    };
    fetchCount();
  }, []);

  return (
    <div className="page-layout">
      <Sidebar />
      <main className={styles.mainContainer}>
        <div className={styles.mainTitle}>
          <h3>DASHBOARD</h3>
        </div>

        <div className={styles.mainCards}>
          {/* Carte cliquable pour afficher la liste */}
          <div
            className={`${styles.card} bg-[#2962ff] cursor-pointer`}
            onClick={() => setShowEtablissements(true)}
          >
            <div className={styles.cardInner}>
              <h3>Etablissements</h3>
              <BsFillGrid3X3GapFill className={styles.cardIcon} />
            </div>
            <h1>{count}</h1>
          </div>

          {/* Autres cartes */}
          <div className={`${styles.card} bg-[#ff6d00]`}>
            <div className={styles.cardInner}>
              <h3>Niveau</h3>
              <BsPeopleFill className={styles.cardIcon} />
            </div>
            <h1>12</h1>
          </div>
        </div>

        {/* Composant EtablissementsComponent (avec modal) */}
        <EtablissementsComponent
          show={showEtablissements}
          onClose={() => setShowEtablissements(false)}
        />

        {/* Bouton Filtrer par */}
        <div className={styles.filterButtonContainer}>
          <button
            id="dropdownDelayButton"
            data-dropdown-toggle="dropdownDelay"
            data-dropdown-delay="500"
            data-dropdown-trigger="hover"
            className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center inline-flex items-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
            type="button"
          >
            Filtrer par{" "}
            <svg
              className="w-2.5 h-2.5 ms-3"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 10 6"
            >
              <path
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="m1 1 4 4 4-4"
              />
            </svg>
          </button>

          <div
            id="dropdownDelay"
            className="z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow-sm w-44 dark:bg-gray-700"
          >
            <ul
              className="py-2 text-sm text-gray-700 dark:text-gray-200"
              aria-labelledby="dropdownDelayButton"
            >
              <li>
                <a
                  href="#"
                  className="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white"
                >
                  Type
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white"
                >
                  Genre
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white"
                >
                  Niveau
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white"
                >
                  Milieu
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Section des graphiques */}
        <div className={styles.charts}>
          <ResponsiveContainer>
            <BarChartLabel />
          </ResponsiveContainer>
          <ResponsiveContainer width="100%" height={300}>
            <PieArcLabel />
          </ResponsiveContainer>
        </div>
      </main>
    </div>
  );
}

export default DashGcomponent;
