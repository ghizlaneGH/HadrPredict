import React from "react";
import { useNavigate } from "react-router-dom";
import logo from "../../components/Assets/logoo.png";
import Dashboard from "../Dashboard";
import Sidebar from "../../components/Layout/Sidebar/Sidebar";

const HomePage = () => {
  return (
    <div
      style={{
        display: "flex",
        height: "100vh",
        width: "100vw",
      }}
    >
      <Sidebar />

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
          width: "100vw",
        }}
      >
        <img
          src="src\components\Assets\logo.png"
          alt="logo"
          style={{
            maxWidth: "100%",
            maxHeight: "80vh",
          }}
        />
      </div>
    </div>
  );
};

export default HomePage;
