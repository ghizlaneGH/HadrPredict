import React from "react";
import { Routes, Route, BrowserRouter, Navigate } from "react-router-dom";

import HomePage from "./pages/Cartegraphie/HomePage";
import GeoCarte from "./pages/Cartegraphie/GeoCart";
import LoginPage from "./pages/Login/LoginPage";
import FormPage from "./pages/Formulaire/FormPage";
import SignupPage from "./pages/Signup/SignupPage";
import DashGlobal from "./pages/DashGlobal";
import AlertsElevesPage from './pages/AlertsElevesPage';


import Dashboard from "./pages/Dashboard";
import DashComponent from "./components/Dash/DashComponent";

function App() {
  return (
    <BrowserRouter>
      <Routes>
      <Route path="/" element={<Navigate to="/LoginPage" replace />} />
        <Route path="/GeoCarte" element={<GeoCarte />}></Route>
        <Route path="/LoginPage" element={<LoginPage />}></Route>
        <Route path="/FormPage" element={<FormPage />}></Route>
        <Route path="/SignupPage" element={<SignupPage />}></Route>
        <Route path="/DashGlobal" element={<DashGlobal />}></Route>
        <Route path="/alertes/:schoolId" element={<AlertsElevesPage/>} />
        <Route path="/Dashboard" element={<Dashboard />} />
        <Route path="/Dashboard/:schoolId" element={<DashComponent />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
