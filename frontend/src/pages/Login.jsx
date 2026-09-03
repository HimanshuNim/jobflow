import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    try {
      await login(email, password);
      navigate("/dashboard");
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
        "Invalid email or password"
      );
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">

        <h1>JobFlow</h1>

        <p className="login-subtitle">
          Job Application Tracker
        </p>

        {error && (
          <p className="login-error">
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit}>

          <div className="login-field">
            <label>Email</label>

            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>

          <div className="login-field">
            <label>Password</label>

            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </div>

          <button
            className="login-button"
            type="submit"
          >
            Login
          </button>

        </form>

        <p className="register-link">
          Don't have an account?{" "}
          <span onClick={() => navigate("/register")}>
            Register
          </span>
        </p>

      </div>
    </div>
  );
}

export default Login;