import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "./Register.css";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");
    if (password.length < 8) {
  setError("Password must be at least 8 characters.");
  return;
}

if (password.length > 100) {
  setError("Password must be 100 characters or less.");
  return;
}

    try {
      await api.post("/users/register", {
        name,
        email,
        password,
      });

      setSuccess("Account created successfully.");

      setTimeout(() => {
        navigate("/login");
      }, 1000);

    } catch (error) {
  console.error(error);

  const message =
    error.response?.data?.message ||
    error.response?.data?.error ||
    "Registration failed";

  setError(message);
}
  };

  return (
    <div className="register-page">
      <div className="register-card">

        <h1>JobFlow</h1>

        <p className="register-subtitle">
          Create your account
        </p>

        {error && (
          <p className="register-error">
            {error}
          </p>
        )}

        {success && (
          <p className="register-success">
            {success}
          </p>
        )}

        <form onSubmit={handleSubmit}>

          <div className="register-field">
            <label>Name</label>

            <input
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              required
            />
          </div>

          <div className="register-field">
            <label>Email</label>

            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>

          <div className="register-field">
            <label>Password</label>

            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </div>

          <button
            className="register-button"
            type="submit"
          >
            Register
          </button>

        </form>

        <p className="login-link">
          Already have an account?{" "}
          <span onClick={() => navigate("/login")}>
            Login
          </span>
        </p>

      </div>
    </div>
  );
}

export default Register;