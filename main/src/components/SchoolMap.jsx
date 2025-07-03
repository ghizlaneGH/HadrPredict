import React from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import schoolIconUrl from "../components/Assets/school.png";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Layout/Sidebar/Sidebar";

const schoolIcon = new L.Icon({
  iconUrl: schoolIconUrl,
  iconSize: [35, 35],
  iconAnchor: [17, 35],
  popupAnchor: [0, -30],
});

const schools = [
  {
    id: 1,
    name: "Lycée Al Irfane",
    lat: 34.6833,
    lng: -1.9094,
    abandonRate: 18,
  },
  {
    id: 2,
    name: "Collège Al Qods",
    lat: 34.6865,
    lng: -1.8994,
    abandonRate: 12,
  },
];

const SchoolMap = () => {
  const navigate = useNavigate();

  return (
    <div className="page-layout">

      <Sidebar />

    <div
    style={{
      height: "100vh",
      width: "100vw",
      overflow: "hidden",
      padding: "16px",
      boxSizing: "border-box",
      backgroundColor: "#f0f0f0",
    }}
    >
      <div
        style={{
          height: "100%",
          borderRadius: "8px",
          boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
          overflow: "hidden",
        }}
      >
        <MapContainer
          center={[34.6833, -1.9094]}
          zoom={13}
          style={{ height: "100%", width: "100%" }}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution="© OpenStreetMap contributors"
          />
          {schools.map((school) => (
            <Marker
              key={school.id}
              position={[school.lat, school.lng]}
              icon={schoolIcon}
              eventHandlers={{
                click: () => {
                  navigate(`/Dashboard/${school.id}`); 
                },
              }}
            >
              <Popup>
                <strong>{school.name}</strong>
                <br />
                Taux d'abandon : {school.abandonRate}%
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </div>
    </div>
  );
};

export default SchoolMap;