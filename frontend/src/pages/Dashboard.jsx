import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import "../Dashboard.css";

function Dashboard() {
  const [applications, setApplications] = useState([]);
  const [summary, setSummary] = useState({
                                          applied: 0,
                                          interview: 0,
                                          offer: 0,
                                          rejected: 0,
                                          total: 0,
                                        });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [user, setUser] = useState(null);

  const [resumes, setResumes] = useState([]);
  const [showResumeForm, setShowResumeForm] = useState(false);
  const [resumeName, setResumeName] = useState("");
  const [resumeFile, setResumeFile] = useState(null);

  const [timeRemaining, setTimeRemaining] = useState(null);

  // ADD THESE HERE
  const [showForm, setShowForm] = useState(false);

  const [editingId, setEditingId] = useState(null);

  const [statusFilter, setStatusFilter] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [sortBy, setSortBy] = useState("companyName");
  const [sortDirection, setSortDirection] = useState("asc");

  const [formData, setFormData] = useState({
  companyName: "",
  jobTitle: "",
  status: "APPLIED",
  jobUrl: "",
  resumeId: "",
});

const {
  logout,
  sessionExpiresAt,
  isAuthenticated,
} = useAuth();
  const navigate = useNavigate();

useEffect(() => {
  if (!isAuthenticated) {
    navigate("/login");
  }
}, [isAuthenticated, navigate]);

useEffect(() => {
  fetchApplications();
}, [statusFilter, page, sortBy, sortDirection]);

useEffect(() => {
  fetchSummary();
}, []);

useEffect(() => {
  fetchProfile();
  fetchResumes();
}, []);

useEffect(() => {
  if (!success) {
    return;
  }

  const timer = setTimeout(() => {
    setSuccess("");
  }, 3000);

  return () => clearTimeout(timer);
}, [success]);

useEffect(() => {
  if (!sessionExpiresAt) {
    setTimeRemaining(null);
    return;
  }

  const updateTimer = () => {
    const remaining = sessionExpiresAt - Date.now();

    if (remaining <= 0) {
      setTimeRemaining(0);
      return;
    }

    setTimeRemaining(remaining);
  };

  updateTimer();

  const interval = setInterval(updateTimer, 1000);

  return () => clearInterval(interval);
}, [sessionExpiresAt]);

const fetchApplications = async () => {
  try {
    let url = `/applications?page=${page}&size=10&sort=${sortBy},${sortDirection}`;

    if (statusFilter) {
      url = `/applications?status=${statusFilter}&page=${page}&size=10&sort=${sortBy},${sortDirection}`;
    }

    const response = await api.get(url);

setApplications(response.data.content);
setTotalPages(response.data.totalPages);
  } catch (error) {
    console.error(error);
    setError(
      error.response?.data?.message ||
      "Failed to load applications"
    );
  }
};

const fetchSummary = async () => {
  try {
    const response = await api.get("/applications/summary");
    setSummary(response.data);
  } catch (error) {
    console.error(error);
  }
};

const fetchProfile = async () => {
  try {
    const response = await api.get("/users/profile");
    setUser(response.data);
  } catch (error) {
    console.error(error);
  }
};

const fetchResumes = async () => {
  try {
    const response = await api.get("/resumes");
    setResumes(response.data);
  } catch (error) {
    console.error(error);
  }
};

  // ADD THESE FUNCTIONS HERE

const handleResumeUpload = async (event) => {
  event.preventDefault();
  setError("");

  if (!resumeFile) {
    setError("Please select a resume file");
    return;
  }

  try {
    const formData = new FormData();

    formData.append("name", resumeName);
    formData.append("file", resumeFile);

    await api.post("/resumes", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    setResumeName("");
    setResumeFile(null);
    setShowResumeForm(false);

    await fetchResumes();
    setSuccess("Resume uploaded successfully");
  } catch (error) {
    console.error(error);
    setError(
      error.response?.data?.message ||
      "Failed to upload resume"
    );
  }
};

const handleResumeDelete = async (id) => {
  const confirmed = window.confirm(
    "Are you sure you want to delete this resume?"
  );

  if (!confirmed) {
    return;
  }

  setError("");

  try {
    await api.delete(`/resumes/${id}`);
    await fetchResumes();
    setSuccess("Resume deleted successfully");
  } catch (error) {
    console.error(error);
    setError(
      error.response?.data?.message ||
      "Failed to delete resume"
    );
  }
};

const handleResumeDownload = async (resume) => {
  try {
    const response = await api.get(
      `/resumes/${resume.id}/download`,
      {
        responseType: "blob",
      }
    );

    const url = window.URL.createObjectURL(
      new Blob([response.data])
    );

    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", resume.fileName);

    document.body.appendChild(link);
    link.click();

    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error(error);
    setError("Failed to download resume");
  }
};

const handleApplicationResumeDownload = async (application) => {
  try {
    const response = await api.get(
      `/resumes/${application.resumeId}/download`,
      {
        responseType: "blob",
      }
    );

    const url = window.URL.createObjectURL(
      new Blob([response.data])
    );

    const link = document.createElement("a");
    link.href = url;
    link.setAttribute(
      "download",
      application.resumeFileName
    );

    document.body.appendChild(link);
    link.click();

    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error(error);
    setError("Failed to download resume");
  }
};
  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = async (event) => 
    {
          event.preventDefault();
          setError("");

          const wasEditing = !!editingId;

          try {
        let response;

        if (editingId) {
          response = await api.put(`/applications/${editingId}`, formData);
        } else {
          response = await api.post("/applications", formData);
        }

            setFormData({
              companyName: "",
              jobTitle: "",
              status: "APPLIED",
              jobUrl: "",
              resumeId: "",
            });
            setEditingId(null);
            setShowForm(false);

            await fetchApplications();
            await fetchSummary();
            

            setSuccess(
              wasEditing
                ? "Application updated successfully"
                : "Application added successfully"
            );
          } catch (error) {
            console.error(error);
            setError(
              error.response?.data?.message ||
              "Failed to save application"
            );
          }
};

  const handleEdit = (application) => {
  setEditingId(application.id);

  setFormData({
  companyName: application.companyName,
  jobTitle: application.jobTitle,
  status: application.status,
  jobUrl: application.jobUrl || "",
  resumeId: application.resumeId || "",
});

  setShowForm(true);
};

const handleDelete = async (id) => {
  const confirmed = window.confirm(
    "Are you sure you want to delete this application?"
  );

  if (!confirmed) {
    return;
  }

  setError("");

  try {
    await api.delete(`/applications/${id}`);

    await fetchApplications();
    await fetchSummary();
setSuccess("Application deleted successfully");
  } catch (error) {
    console.error(error);
    setError(
      error.response?.data?.message ||
      "Failed to delete application"
    );
  }
};

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

const formatTimeRemaining = (milliseconds) => {
  if (milliseconds === null) {
    return "";
  }

  if (milliseconds <= 0) {
    return "Expired";
  }

  const totalSeconds = Math.floor(milliseconds / 1000);

  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, "0")}:${String(
      seconds
    ).padStart(2, "0")}`;
  }

  return `${minutes}:${String(seconds).padStart(2, "0")}`;
};

  return (
  <div className="dashboard">

    <div className="dashboard-header">
      <div>
        <h1>JobFlow</h1>
        <p>Job Application Tracker</p>
        {user && (
      <p className="welcome-text">
        Welcome, {user.name}
      </p>
    )}
      </div>

      <div className="header-actions">

  <button
    className="logout-button"
    onClick={handleLogout}
  >
    Logout
  </button>

  {timeRemaining !== null && (
    <p className="session-timer">
      {timeRemaining > 0 ? (
        <>
          Session expires in{" "}
          <strong>{formatTimeRemaining(timeRemaining)}</strong>
        </>
      ) : (
        <>
          ⚠️ <strong>Session expired</strong>
        </>
      )}
    </p>
  )}

</div>
    </div>

    {error && (
      <p className="error-message">
        {error}
      </p>
    )}

    {success && (
  <p className="success-message">
    ✓ {success}
  </p>
)}

    <div className="summary">

      <div className="summary-card">
        <h3>Total Applications</h3>
        <p>{summary.total}</p>
      </div>

      <div className="summary-card">
        <h3>Applied</h3>
        <p>{summary.applied}</p>
      </div>

      <div className="summary-card">
        <h3>Interview</h3>
        <p>{summary.interview}</p>
      </div>

      <div className="summary-card">
        <h3>Offer</h3>
        <p>{summary.offer}</p>
      </div>

      <div className="summary-card">
        <h3>Rejected</h3>
        <p>{summary.rejected}</p>
      </div>

    </div>


    {showForm && (
      <form className="application-form" onSubmit={handleSubmit}>

        <div className="form-group">
          <label>Company Name</label>

          <input
            name="companyName"
            value={formData.companyName}
            onChange={handleChange}
            required
          />
        </div>



        <div className="form-group">
          <label>Job Title</label>

          <input
            name="jobTitle"
            value={formData.jobTitle}
            onChange={handleChange}
            required
          />
        </div>



        <div className="form-group">
          <label>Status</label>


          <select
            name="status"
            value={formData.status}
            onChange={handleChange}
          >
            <option value="APPLIED">APPLIED</option>
            <option value="INTERVIEW">INTERVIEW</option>
            <option value="OFFER">OFFER</option>
            <option value="REJECTED">REJECTED</option>
          </select>
        </div>



        <div className="form-group">
          <label>Job URL</label>


          <input
            type="url"
            name="jobUrl"
            value={formData.jobUrl}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
  <label>Resume</label>


  <select
  name="resumeId"
  value={formData.resumeId}
  onChange={(event) =>
    setFormData({
      ...formData,
      resumeId: event.target.value
        ? Number(event.target.value)
        : "",
    })
  }
>
    <option value="">No Resume</option>

    {resumes.map((resume) => (
      <option key={resume.id} value={resume.id}>
        {resume.name} — v{resume.version}
      </option>
    ))}
  </select>
</div>



        <div className="form-actions">
        <button type="submit">
          {editingId ? "Update Application" : "Save Application"}
        </button>

      <button
        type="button"
        onClick={() => {
          setShowForm(false);
          setEditingId(null);
        }}
      >
        Cancel
      </button>
    </div>

      </form>
    )}

<div className="section-header">
  <h2>Resumes</h2>

  <button
    className="add-button"
    onClick={() => setShowResumeForm(!showResumeForm)}
  >
    {showResumeForm ? "Cancel" : "+ Upload Resume"}
  </button>
</div>

<div className="resume-section">


  {showResumeForm && (
    <form
      className="application-form"
      onSubmit={handleResumeUpload}
    >

      <div className="form-group">
        <label>Resume Name</label>


        <input
          type="text"
          value={resumeName}
          onChange={(event) =>
            setResumeName(event.target.value)
          }
          placeholder="e.g. Java Backend Resume"
          required
        />
      </div>



      <div className="form-group">
        <label>Resume File</label>


        <input
          type="file"
          accept=".pdf,.docx"
          onChange={(event) =>
            setResumeFile(event.target.files[0])
          }
          required
        />
      </div>

      <div className="form-actions">
        <button type="submit">
          Upload Resume
        </button>

        <button
          type="button"
          onClick={() => {
            setShowResumeForm(false);
            setResumeName("");
            setResumeFile(null);
          }}
        >
          Cancel
        </button>
      </div>

    </form>
  )}

  {resumes.length === 0 ? (
    <p>No resumes uploaded.</p>
  ) : (
    <div className="application-list">
      <table className="application-table">

        <thead>
          <tr>
            <th>Name</th>
            <th>File</th>
            <th>Version</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {resumes.map((resume) => (
            <tr key={resume.id}>

              <td>{resume.name}</td>

              <td>{resume.fileName}</td>

              <td>v{resume.version}</td>

              <td>
                {new Date(
                  resume.createdAt
                ).toLocaleDateString()}
              </td>

              <td>
                <div className="application-actions">

                  <button
                    onClick={() =>
                      handleResumeDownload(resume)
                    }
                  >
                    Download
                  </button>

                  <button
                    onClick={() =>
                      handleResumeDelete(resume.id)
                    }
                  >
                    Delete
                  </button>

                </div>
              </td>

            </tr>
          ))}
        </tbody>

      </table>
    </div>
  )}

</div>

<div className="section-header">
  <h2>Applications</h2>

  <button
    className="add-button"
    onClick={() => setShowForm(!showForm)}
  >
    {showForm ? "Cancel" : "+ Add Application"}
  </button>
</div>


    <div className="toolbar">

      <div>
        <label>Filter by status: </label>

        <select
          value={statusFilter}
          onChange={(event) => {
            setStatusFilter(event.target.value);
            setPage(0);
          }}
        >
          <option value="">All Statuses</option>
          <option value="APPLIED">APPLIED</option>
          <option value="INTERVIEW">INTERVIEW</option>
          <option value="OFFER">OFFER</option>
          <option value="REJECTED">REJECTED</option>
        </select>
      </div>

      <div>
        <label>Sort by: </label>

        <select
          value={sortBy}
          onChange={(event) => {
            setSortBy(event.target.value);
            setPage(0);
          }}
        >
          <option value="companyName">Company Name</option>
          <option value="jobTitle">Job Title</option>
          <option value="status">Status</option>
          <option value="appliedAt">Applied Date</option>
        </select>

        <select
          value={sortDirection}
          onChange={(event) => {
            setSortDirection(event.target.value);
            setPage(0);
          }}
        >
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>
      </div>
    </div>

    {applications.length === 0 ? (
  <p>No applications found.</p>
) : (
  <div className="application-list">
    <table className="application-table">
      <thead>
        <tr>
          <th>Company</th>
          <th>Job Title</th>
          <th>Status</th>
          <th>Resume</th>
          <th>Applied Date</th>
          <th>Actions</th>
        </tr>
      </thead>

      <tbody>
        {applications.map((application) => (
          <tr key={application.id}>
            <td>{application.companyName}</td>

            <td>{application.jobTitle}</td>

            <td>
              <span className={`status status-${application.status.toLowerCase()}`}>
                {application.status}
              </span>
            </td>

            <td>
              {application.resumeId ? (
                <button
                  type="button"
                  className="resume-download-button"
                  onClick={() =>
                    handleApplicationResumeDownload(application)
                  }
                >
                  {application.resumeName} (v{application.resumeVersion})
                </button>
              ) : (
                "-"
              )}
            </td>

            <td>
              {application.appliedAt
                ? new Date(application.appliedAt).toLocaleDateString()
                : "-"}
            </td>

            <td>
              <div className="application-actions">
                <button
                  onClick={() => handleEdit(application)}
                >
                  Edit
                </button>

                <button
                  onClick={() => handleDelete(application.id)}
                >
                  Delete
                </button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
)}

    <div className="pagination">

      <button
        onClick={() => setPage(page - 1)}
        disabled={page === 0}
      >
        Previous
      </button>

      <span>
        Page {page + 1} of {totalPages}
      </span>

      <button
        onClick={() => setPage(page + 1)}
        disabled={page >= totalPages - 1}
      >
        Next
      </button>

    </div>

  </div>
);
}

export default Dashboard;