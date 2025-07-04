import React, { useState } from "react";
import "./Signup.css";
import logo from "../Assets/logoo.png";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const Signup = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    lastName: "",
    firstName: "",
    email: "",
    password: "",
    confirmPassword: ""
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.id]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password !== form.confirmPassword) {
      alert("Les mots de passe ne correspondent pas.");
      return;
    }

    try {
      const response = await axios.post("http://localhost:8081/auth/register", {
        lastName: form.lastName,
        firstName: form.firstName,
        email: form.email,
        password: form.password
      });

      alert("Inscription réussie !");
      navigate("/login");
    } catch (error) {
      alert("Erreur lors de l'inscription : " + (error.response?.data || error.message));
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-header">
        <img src={logo} alt="logo" />
      </div>

      <form className="inputs" onSubmit={handleSubmit}>
        <div className="input">
          <label htmlFor="lastName">Nom :</label>
          <input
            type="text"
            id="lastName"
            value={form.lastName}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input">
          <label htmlFor="firstName">Prénom :</label>
          <input
            type="text"
            id="firstName"
            value={form.firstName}
            onChange={handleChange}
            required
          />
        </div>
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
        <div className="input">
          <label htmlFor="confirmPassword">Confirmer le mot de passe :</label>
          <input
            type="password"
            id="confirmPassword"
            value={form.confirmPassword}
            onChange={handleChange}
            required
          />
        </div>
        <button type="submit">S'inscrire</button>
      </form>

      <div className="login-redirect">
        <p>Vous avez déjà un compte ? <Link to="/LoginPage">Connectez-vous</Link></p>
      </div>

    </div>
  );
};

export default Signup;
