import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Layout/Sidebar/Sidebar";

const schoolIcon = L.icon({
  iconUrl: "/public/icon.jpeg",
  iconSize: [14, 8],
  iconAnchor: [7, 8],
  popupAnchor: [0, -8],
});

const SchoolMap = () => {
  const [schools, setSchools] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8081/api/schools/map")
      .then((res) => res.json())
      .then((data) => setSchools(data))
      .catch((err) => console.error("Erreur chargement écoles:", err));
  }, []);

  const filteredSchools = schools.filter((school) =>
    school.nom.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="page-layout" style={{ display: "flex", height: "100vh" }}>
      <Sidebar />
      <div
        style={{
          flex: 1,
          padding: "16px",
          boxSizing: "border-box",
          backgroundColor: "#f0f0f0",
          borderRadius: "8px",
          boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
          position: "relative",
          overflow: "hidden",
        }}
      >
        <input
          type="text"
          placeholder="Rechercher une école..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={{
            position: "absolute",
            top: "24px",
            left: "70px",
            width: "350px",
            height: "40px",
            padding: "10px 14px",
            fontSize: "16px",
            border: "1px solid #ccc",
            borderRadius: "6px",
            backgroundColor: "white",
            boxShadow: "0 3px 8px rgba(0,0,0,0.2)",
            zIndex: 1000,
          }}
        />

        <MapContainer
          center={[34.6833, -1.9094]}
          zoom={8}
          style={{ height: "100%", width: "100%" }}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution="© OpenStreetMap contributors"
          />
          {filteredSchools.map((school) => (
            <Marker
              key={school.id}
              position={[school.latitude, school.longitude]}
              icon={schoolIcon}
              eventHandlers={{
                click: () => {
                  navigate(`/Dashboard/${encodeURIComponent(school.id)}`);
                },
              }}
            >
              <Popup>
                <div style={{ fontSize: "14px" }}>
                  <strong>{school.nom}</strong>
                  <br />
                  📍 {school.commune}, {school.province}
                  <br />
                  🏫 {school.typeSchool}
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </div>
  );
};

export default SchoolMap;
