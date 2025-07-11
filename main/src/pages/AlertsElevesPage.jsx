import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function AlertsElevesPage() {
  const { schoolId } = useParams();
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      const url =
        schoolId && schoolId !== "global"
          ? `http://localhost:8081/api/eleve/alertes/ecole/${schoolId}`
          : `http://localhost:8081/api/eleve/alertes/global`;

      try {
        const res = await fetch(url);
        const data = await res.json();
        setStudents(data);
      } catch (err) {
        console.error("Erreur de chargement:", err);
        setStudents([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [schoolId]);

  return (
    <div style={{ padding: "20px" }}>
      <h2>Liste des eleves en risque d'abandon</h2>

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ backgroundColor: "#f2f2f2" }}>
            <th>ID Élève</th>
            <th>Résultat</th>
            <th>Absences</th>
            <th>Situation</th>
          </tr>
        </thead>
        <tbody>
          {students.length === 0 && (
            <tr>
              <td colSpan="3" style={{ textAlign: "center" }}>
                Aucun eleve en alerte
              </td>
            </tr>
          )}

          {students.map((student) => (
            <tr key={student.id}>
              <td style={{ padding: "10px", border: "1px solid #ccc" }}>
                {student.id}
              </td>
              <td style={{ padding: "10px", border: "1px solid #ccc" }}>
                {student.resultat}
              </td>
              <td style={{ padding: "10px", border: "1px solid #ccc" }}>
                {student.absence}
              </td>
              <td style={{ padding: "10px", border: "1px solid #ccc" }}>
                {student.situation}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
