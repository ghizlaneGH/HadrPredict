import { useState, useEffect } from "react";
import axios from "axios";
import styles from "./Dash.module.css";
import { BsFillGrid3X3GapFill } from "react-icons/bs";
import { Link } from "react-router-dom";

const EtablissementsComponent = ({ show, onClose }) => {
  const [provinces, setProvinces] = useState([]);
  const [communes, setCommunes] = useState([]);
  const [etablissements, setEtablissements] = useState([]);
  const [selectedProvince, setSelectedProvince] = useState("");
  const [selectedCommune, setSelectedCommune] = useState("");
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  // Calculs distincts pour chaque liste
  const indexOfLastCommune = currentPage * itemsPerPage;
  const indexOfFirstCommune = indexOfLastCommune - itemsPerPage;
  const currentCommunes = communes.slice(
    indexOfFirstCommune,
    indexOfLastCommune
  );

  const indexOfLastEtablissement = currentPage * itemsPerPage;
  const indexOfFirstEtablissement = indexOfLastEtablissement - itemsPerPage;
  const currentEtablissements = etablissements.slice(
    indexOfFirstEtablissement,
    indexOfLastEtablissement
  );

  // Navigation
  const nextPage = () => {
    if (selectedCommune && indexOfLastEtablissement < etablissements.length) {
      setCurrentPage((prev) => prev + 1);
    } else if (!selectedCommune && indexOfLastCommune < communes.length) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const prevPage = () => {
    if (currentPage > 1) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  // Chargement des provinces
  const fetchProvinces = async () => {
    setLoading(true);
    try {
      const res = await axios.get("/api/schools/provinces", {
        withCredentials: true,
      });
      setProvinces(res.data);
    } catch (err) {
      console.error("Erreur lors du chargement des provinces", err);
    } finally {
      setLoading(false);
    }
  };

  // Chargement des communes
  const fetchCommunes = async (province) => {
    setLoading(true);
    try {
      const res = await axios.get(`/api/schools/communes/${province}`, {
        withCredentials: true,
      });
      setCommunes(res.data);
      setSelectedCommune("");
      setEtablissements([]);
      setCurrentPage(1); // Réinitialiser la page
    } catch (err) {
      console.error("Erreur lors du chargement des communes", err);
    } finally {
      setLoading(false);
    }
  };

  // Chargement des établissements
  const fetchEtablissements = async (commune) => {
    setLoading(true);
    try {
      const res = await axios.get(`/api/schools/liste/${commune}`, {
        withCredentials: true,
      });
      console.log("Réponse de l'API :", res.data);

      if (Array.isArray(res.data)) {
        setEtablissements(res.data);
      } else {
        console.error("Les données reçues ne sont pas un tableau");
        setEtablissements([]);
      }

      setCurrentPage(1); // Réinitialiser la page
    } catch (err) {
      console.error("Erreur lors du chargement des etablissements", err);
      setEtablissements([]);
    } finally {
      setLoading(false);
    }
  };

  // Charger les provinces quand on ouvre
  useEffect(() => {
    if (show) {
      fetchProvinces();
    }
  }, [show]);

  if (!show) return null;

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button className={styles.closeButton} onClick={onClose}>
          &times;
        </button>
        <h2>Liste des Établissements</h2>

        {loading ? (
          <p>Chargement...</p>
        ) : !selectedProvince ? (
          <>
            <h3>Sélectionnez une province :</h3>
            {provinces.length > 0 ? (
              <ul>
                {provinces.map((p, i) => (
                  <li
                    key={i}
                    onClick={() => {
                      setSelectedProvince(p);
                      fetchCommunes(p);
                    }}
                    className="cursor-pointer hover:underline"
                  >
                    {p}
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucune province trouvée.</p>
            )}
          </>
        ) : !selectedCommune ? (
          <>
            <h3>Sélectionnez une commune :</h3>
            {communes.length > 0 ? (
              <>
                <ul>
                  {currentCommunes.map((c, i) => (
                    <li
                      key={i}
                      onClick={() => {
                        setSelectedCommune(c);
                        fetchEtablissements(c);
                      }}
                      className="cursor-pointer hover:underline"
                    >
                      {c}
                    </li>
                  ))}
                </ul>

                {/* Pagination pour les communes */}
                <div className="pagination-controls">
                  <button onClick={prevPage} disabled={currentPage === 1}>
                    Précédent
                  </button>
                  <span> Page {currentPage} </span>
                  <button
                    onClick={nextPage}
                    disabled={indexOfLastCommune >= communes.length}
                  >
                    Suivant
                  </button>
                </div>
              </>
            ) : (
              <p>Aucune commune trouvée.</p>
            )}
          </>
        ) : (
          <>
            {Array.isArray(etablissements) && etablissements.length > 0 ? (
              <>
                <ul>
                  {currentEtablissements.map((etablissement) => (
                    <li key={etablissement.id}>
                      <Link to={`/dashboard/${etablissement.id}`}>
                        {etablissement.nom}
                      </Link>
                    </li>
                  ))}
                </ul>

                {/* Pagination pour les établissements */}
                <div className="pagination-controls">
                  <button onClick={prevPage} disabled={currentPage === 1}>
                    Precedent
                  </button>
                  <span> Page {currentPage} </span>
                  <button
                    onClick={nextPage}
                    disabled={indexOfLastEtablissement >= etablissements.length}
                  >
                    Suivant
                  </button>
                </div>
              </>
            ) : (
              <p>Aucun établissement trouvé.</p>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default EtablissementsComponent;
