import React, { useState } from "react";
import "./Login.css";
import logo from "../Assets/logoo.png";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const Login = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    password: "",
    rememberMe: false,
  });

  const handleChange = (e) => {
    const { id, value, type, checked } = e.target;
    setForm({
      ...form,
      [id]: type === "checkbox" ? checked : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:8081/auth/login", {
        email: form.email,
        password: form.password},{
        headers: {
          "Content-Type": "application/json",
        }
      });

      alert("Connexion réussie !");
      navigate("/GeoCarte");
    } catch (error) {
      alert(
        "Erreur lors de la connexion : " +
          (error.response?.data || error.message)
      );
    }
  };

  return (
    <div className="login-container">
      <div className="login-header">
        <img src={logo} alt="logo" />
      </div>
      <form className="inputs" onSubmit={handleSubmit}>
        <div className="input">
          <label htmlFor="email">Email :</label>
          <input
            type="email"
            id="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input">
          <label htmlFor="password">Mot de passe :</label>
          <input
            type="password"
            id="password"
            value={form.password}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-options">
          <label className="remember-me">
            <input
              type="checkbox"
              id="rememberMe"
              checked={form.rememberMe}
              onChange={handleChange}
            />
            <span className="checkmark"></span>
            Se souvenir de moi
          </label>
          <Link to="/forgot-password">Mot de passe oublié ?</Link>
        </div>
        <button type="submit">Login</button>
      </form>

      <div className="register-redirect">
        <p>
          Vous n'avez pas de compte ?{" "}
          <Link to="/SignupPage">Inscrivez-vous</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
