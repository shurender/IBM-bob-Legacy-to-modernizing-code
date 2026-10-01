import { FormEvent, useEffect, useState } from "react";
import { createApplication, getApplications } from "./api";
import type { ApplicationInput, LoanApplication, ValidationResult } from "./types";

const initialForm: ApplicationInput = {
  customerName: "",
  age: "",
  employmentType: "",
  monthlyIncome: "",
  creditScore: "",
  existingEmi: "",
  loanAmount: "",
  loanTenure: ""
};

type View = "form" | "result" | "applications" | "detail";

function formatCurrency(val: number | string | undefined): string {
  if (val === undefined || val === null || val === "") return "0";
  const num = Number(val);
  if (isNaN(num)) return String(val);
  return num.toLocaleString("en-IN");
}

function employmentLabel(type: string | undefined): string {
  switch (type) {
    case "FULL_TIME":
      return "Full Time";
    case "PART_TIME":
      return "Part Time";
    case "SELF_EMPLOYED":
      return "Self Employed";
    case "CONTRACT":
      return "Contract";
    case "UNEMPLOYED":
      return "Unemployed";
    default:
      return type || "";
  }
}

export function App() {
  const [view, setView] = useState<View>("form");
  const [form, setForm] = useState<ApplicationInput>(initialForm);
  const [validation, setValidation] = useState<ValidationResult | null>(null);
  const [result, setResult] = useState<LoanApplication | null>(null);
  const [applications, setApplications] = useState<LoanApplication[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [detail, setDetail] = useState<LoanApplication | null>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (view === "applications") {
      void loadApplications();
    }
  }, [view]);

  async function loadApplications() {
    try {
      setBusy(true);
      const data = await getApplications();
      setApplications(data.applicationList || []);
      setTotalCount(data.totalCount || 0);
    } catch {
      setApplications([]);
      setTotalCount(0);
    } finally {
      setBusy(false);
    }
  }

  function updateField(name: keyof ApplicationInput, value: string) {
    setForm((current) => ({ ...current, [name]: value }));
  }

  function newApplication() {
    setForm(initialForm);
    setValidation(null);
    setResult(null);
    setView("form");
  }

  function openDetail(app: LoanApplication) {
    setDetail(app);
    setView("detail");
  }

  async function submit(event: FormEvent) {
    event.preventDefault();

    // Client-side validation mirroring validateForm()
    if (!form.customerName || form.customerName.trim().length === 0) {
      alert("Please enter the Customer Name.");
      return;
    }
    const ageNum = parseInt(form.age, 10);
    if (!form.age || isNaN(ageNum) || ageNum < 1) {
      alert("Please enter a valid Age.");
      return;
    }
    const emp = form.employmentType;
    if (!emp || emp.trim().length === 0) {
      alert("Please select an Employment Type.");
      return;
    }
    const incomeNum = parseFloat(form.monthlyIncome);
    if (!form.monthlyIncome || isNaN(incomeNum) || incomeNum < 0) {
      alert("Please enter a valid Monthly Income.");
      return;
    }
    const csNum = parseInt(form.creditScore, 10);
    if (!form.creditScore || isNaN(csNum) || csNum < 300 || csNum > 900) {
      alert("Please enter a valid Credit Score (300-900).");
      return;
    }
    const emiNum = parseFloat(form.existingEmi);
    if (form.existingEmi === "" || isNaN(emiNum) || emiNum < 0) {
      alert("Please enter the Existing EMI amount (enter 0 if none).");
      return;
    }
    const laNum = parseFloat(form.loanAmount);
    if (!form.loanAmount || isNaN(laNum) || laNum <= 0) {
      alert("Please enter a valid Loan Amount.");
      return;
    }
    const lt = form.loanTenure;
    if (!lt || lt.trim().length === 0) {
      alert("Please select a Loan Tenure.");
      return;
    }

    try {
      setBusy(true);
      setValidation(null);
      const response = await createApplication(form);
      if (response.validationResult && response.validationResult.errors && response.validationResult.errors.length > 0) {
        setValidation(response.validationResult);
      } else if (response.loanApplication) {
        setResult(response.loanApplication);
        setView("result");
      }
    } catch {
      setValidation({
        valid: false,
        errors: ["Unable to process application. Please try again."],
        warnings: []
      });
    } finally {
      setBusy(false);
    }
  }

  return (
    <div>
      {/* HEADER */}
      <div id="header">
        <div id="header-inner">
          <h1>NBFC LOAN ELIGIBILITY SYSTEM - DEMO</h1>
          <div className="subtitle">
            {view === "detail"
              ? "Demo Page | National Banking & Finance Corporation | Application Detail View"
              : "Demo Page | National Banking & Finance Corporation | Internal Operations Portal"}
          </div>
        </div>
      </div>

      {/* NAVIGATION */}
      <div id="nav-bar">
        <a href="#" onClick={(e) => { e.preventDefault(); newApplication(); }}>Home</a>
      </div>

      {/* VIEWS */}
      {view === "form" && (
        <FormView
          form={form}
          updateField={updateField}
          submit={submit}
          validation={validation}
          busy={busy}
        />
      )}

      {view === "result" && result && (
        <ResultView
          application={result}
          newApplication={newApplication}
          setView={setView}
        />
      )}

      {view === "applications" && (
        <ApplicationsListView
          applications={applications}
          totalCount={totalCount}
          busy={busy}
          newApplication={newApplication}
          openDetail={openDetail}
        />
      )}

      {view === "detail" && detail && (
        <DetailView
          application={detail}
          setView={setView}
          newApplication={newApplication}
        />
      )}

      {/* FOOTER */}
      <div id="footer">
        NBFC Loan Eligibility System &nbsp;|&nbsp; Version 1.0.0 &nbsp;|&nbsp;
        &copy; 2010 National Banking &amp; Finance Corporation &nbsp;|&nbsp;
        All Rights Reserved
      </div>
    </div>
  );
}

function FormView({
  form,
  updateField,
  submit,
  validation,
  busy
}: {
  form: ApplicationInput;
  updateField: (name: keyof ApplicationInput, value: string) => void;
  submit: (event: FormEvent) => void;
  validation: ValidationResult | null;
  busy: boolean;
}) {
  return (
    <div id="page-wrapper">
      <div id="main-content">
        <div className="section-title">DEMO LOAN ELIGIBILITY APPLICATION FORM</div>

        <div className="section-content">
          {validation && validation.errors && validation.errors.length > 0 && (
            <div className="error-box">
              <div className="error-title">Please correct the following errors:</div>
              <ul>
                {validation.errors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </div>
          )}

          <div className="info-box">
            <strong>Instructions:</strong> Please fill in all required fields marked with{" "}
            <span style={{ color: "#cc0000" }}>*</span> and click <strong>CHECK ELIGIBILITY</strong>{" "}
            to receive an instant eligibility assessment.
          </div>

          <form onSubmit={submit}>
            <table className="form-table">
              <tbody>
                {/* SECTION: Customer Information */}
                <tr>
                  <td colSpan={3} style={{ paddingTop: "15px", paddingBottom: "5px" }}>
                    <span style={{ fontWeight: "bold", color: "#003399", fontSize: "13px" }}>
                      &#9658; Customer Information
                    </span>
                    <hr style={{ border: "none", borderTop: "1px solid #ccddff", marginTop: "4px" }} />
                  </td>
                </tr>

                <tr>
                  <td className="label-col">
                    Customer Name <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <input
                      type="text"
                      name="customerName"
                      maxLength={100}
                      value={form.customerName}
                      onChange={(e) => updateField("customerName", e.target.value)}
                    />
                  </td>
                  <td className="small-text">Full name as per ID proof</td>
                </tr>

                <tr>
                  <td className="label-col">
                    Age (Years) <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <input
                      type="number"
                      name="age"
                      min="18"
                      max="120"
                      value={form.age}
                      onChange={(e) => updateField("age", e.target.value)}
                    />
                  </td>
                  <td className="small-text">Must be between 21 and 60</td>
                </tr>

                <tr>
                  <td className="label-col">
                    Employment Type <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <select
                      name="employmentType"
                      value={form.employmentType}
                      onChange={(e) => updateField("employmentType", e.target.value)}
                    >
                      <option value="">-- Select --</option>
                      <option value="FULL_TIME">Full Time</option>
                      <option value="PART_TIME">Part Time</option>
                      <option value="SELF_EMPLOYED">Self Employed</option>
                      <option value="CONTRACT">Contract</option>
                      <option value="UNEMPLOYED">Unemployed</option>
                    </select>
                  </td>
                  <td className="small-text">&nbsp;</td>
                </tr>

                {/* SECTION: Financial Information */}
                <tr>
                  <td colSpan={3} style={{ paddingTop: "15px", paddingBottom: "5px" }}>
                    <span style={{ fontWeight: "bold", color: "#003399", fontSize: "13px" }}>
                      &#9658; Financial Information
                    </span>
                    <hr style={{ border: "none", borderTop: "1px solid #ccddff", marginTop: "4px" }} />
                  </td>
                </tr>

                <tr>
                  <td className="label-col">
                    Monthly Income (&#8377;) <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <input
                      type="number"
                      name="monthlyIncome"
                      min="0"
                      step="1000"
                      value={form.monthlyIncome}
                      onChange={(e) => updateField("monthlyIncome", e.target.value)}
                    />
                  </td>
                  <td className="small-text">Gross monthly income in INR</td>
                </tr>

                <tr>
                  <td className="label-col">
                    Credit Score <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <input
                      type="number"
                      name="creditScore"
                      min="300"
                      max="900"
                      value={form.creditScore}
                      onChange={(e) => updateField("creditScore", e.target.value)}
                    />
                  </td>
                  <td className="small-text">CIBIL score (300 - 900)</td>
                </tr>

                <tr>
                  <td className="label-col">
                    Existing EMI / Month (&#8377;) <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <input
                      type="number"
                      name="existingEmi"
                      min="0"
                      step="500"
                      value={form.existingEmi}
                      onChange={(e) => updateField("existingEmi", e.target.value)}
                    />
                  </td>
                  <td className="small-text">Enter 0 if no existing loans</td>
                </tr>

                {/* SECTION: Loan Details */}
                <tr>
                  <td colSpan={3} style={{ paddingTop: "15px", paddingBottom: "5px" }}>
                    <span style={{ fontWeight: "bold", color: "#003399", fontSize: "13px" }}>
                      &#9658; Loan Details
                    </span>
                    <hr style={{ border: "none", borderTop: "1px solid #ccddff", marginTop: "4px" }} />
                  </td>
                </tr>

                <tr>
                  <td className="label-col">
                    Requested Loan Amount (&#8377;) <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <input
                      type="number"
                      name="loanAmount"
                      min="1"
                      step="1"
                      value={form.loanAmount}
                      onChange={(e) => updateField("loanAmount", e.target.value)}
                    />
                  </td>
                  <td className="small-text">Amount requested in INR</td>
                </tr>

                <tr>
                  <td className="label-col">
                    Loan Tenure (Months) <span className="required">*</span>
                  </td>
                  <td className="field-col">
                    <select
                      name="loanTenure"
                      value={form.loanTenure}
                      onChange={(e) => updateField("loanTenure", e.target.value)}
                    >
                      <option value="">-- Select Tenure --</option>
                      <option value="12">12 months (1 year)</option>
                      <option value="24">24 months (2 years)</option>
                      <option value="36">36 months (3 years)</option>
                      <option value="48">48 months (4 years)</option>
                      <option value="60">60 months (5 years)</option>
                      <option value="84">84 months (7 years)</option>
                      <option value="120">120 months (10 years)</option>
                      <option value="180">180 months (15 years)</option>
                      <option value="240">240 months (20 years)</option>
                    </select>
                  </td>
                  <td className="small-text">&nbsp;</td>
                </tr>
              </tbody>
            </table>

            <div className="button-row">
              <input
                type="submit"
                value={busy ? "CHECKING..." : "CHECK ELIGIBILITY"}
                className="btn-submit"
                disabled={busy}
              />
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

function ResultView({
  application,
  newApplication,
  setView
}: {
  application: LoanApplication;
  newApplication: () => void;
  setView: (view: View) => void;
}) {
  return (
    <div id="page-wrapper">
      <div id="main-content">
        <div className="section-title">DEMO LOAN ELIGIBILITY RESULT</div>

        <div className="section-content">
          <div className="result-box">
            <div className="result-title-bar">
              ========================================
              &nbsp;&nbsp;&nbsp;LOAN ELIGIBILITY DECISION&nbsp;&nbsp;&nbsp;
              ========================================
            </div>

            <div className="result-customer-info">
              <table>
                <tbody>
                  <tr>
                    <td className="info-label">Customer Name:</td>
                    <td><strong>{application.customerName}</strong></td>
                    <td className="info-label">Application Ref:</td>
                    <td className="small-text">{application.applicationReference}</td>
                  </tr>
                  <tr>
                    <td className="info-label">Age:</td>
                    <td>{application.age} years</td>
                    <td className="info-label">Employment:</td>
                    <td>{employmentLabel(application.employmentType)}</td>
                  </tr>
                  <tr>
                    <td className="info-label">Monthly Income:</td>
                    <td>&#8377;{formatCurrency(application.monthlyIncome)}</td>
                    <td className="info-label">Credit Score:</td>
                    <td><strong>{application.creditScore}</strong></td>
                  </tr>
                  <tr>
                    <td className="info-label">Loan Amount Requested:</td>
                    <td><strong>&#8377;{formatCurrency(application.loanAmount)}</strong></td>
                    <td className="info-label">Existing EMI:</td>
                    <td>&#8377;{formatCurrency(application.existingEmi)}</td>
                  </tr>
                  <tr>
                    <td className="info-label">Loan Tenure:</td>
                    <td>{application.loanTenure} months</td>
                    <td className="info-label">&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <hr className="result-separator" />

            <div className="decision-section">
              <div className="decision-label">DECISION:</div>

              {application.decision === "ELIGIBLE" && (
                <div className="decision-eligible">
                  <div className="decision-icon">&#10003;</div>
                  <div className="decision-text">ELIGIBLE FOR LOAN</div>
                  <div className="decision-detail">{application.decisionReason}</div>
                </div>
              )}

              {application.decision === "NOT_ELIGIBLE" && (
                <div className="decision-not-eligible">
                  <div className="decision-icon">&#10007;</div>
                  <div className="decision-text">NOT ELIGIBLE</div>
                  <div className="reason-label">REASON:</div>
                  <div className="reason-text">{application.decisionReason}</div>
                </div>
              )}

              {application.decision === "REVIEW_REQUIRED" && (
                <div className="decision-review">
                  <div className="decision-icon">&#9888;</div>
                  <div className="decision-text">MORE INFORMATION REQUIRED</div>
                  <div className="review-detail">
                    This application cannot be automatically approved or rejected.<br />
                    Additional verification is required.<br /><br />
                    The application has been sent for human review.<br /><br />
                    <em>Reason: {application.decisionReason}</em>
                  </div>
                </div>
              )}
            </div>

            <div className="app-reference">
              Saved SQL Record ID: <strong>{application.id}</strong>
              &nbsp;|&nbsp;
              Reference: <strong>{application.applicationReference}</strong>
              &nbsp;|&nbsp;
              localhost
              &nbsp;|&nbsp;
              Decision recorded in system.
            </div>
          </div>

          <div className="button-row">
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                newApplication();
              }}
              className="btn-secondary"
            >
              &#171; New Application
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}

function ApplicationsListView({
  applications,
  totalCount,
  busy,
  newApplication,
  openDetail
}: {
  applications: LoanApplication[];
  totalCount: number;
  busy: boolean;
  newApplication: () => void;
  openDetail: (app: LoanApplication) => void;
}) {
  return (
    <div id="page-wrapper">
      <div id="main-content">
        <div className="section-title">DEMO SQL DATABASE APPLICATIONS</div>

        <div className="section-content">
          <div className="info-box">
            <strong>Total Applications Saved in SQL Database:</strong> {totalCount}
          </div>

          {busy ? (
            <div style={{ padding: "20px", textAlign: "center", color: "#666666" }}>
              Loading applications...
            </div>
          ) : applications.length === 0 ? (
            <div style={{ padding: "20px", textAlign: "center", color: "#666666" }}>
              No loan applications have been submitted yet.
              <br /><br />
              <a
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  newApplication();
                }}
              >
                Submit the first application
              </a>
            </div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Reference</th>
                  <th>Customer Name</th>
                  <th>Age</th>
                  <th>Income (&#8377;)</th>
                  <th>Credit Score</th>
                  <th>Loan Amt (&#8377;)</th>
                  <th>Decision</th>
                  <th>Date</th>
                  <th>Detail</th>
                </tr>
              </thead>
              <tbody>
                {applications.map((app) => (
                  <tr key={app.id}>
                    <td>{app.id}</td>
                    <td className="small-text">{app.applicationReference}</td>
                    <td>{app.customerName}</td>
                    <td>{app.age}</td>
                    <td>{formatCurrency(app.monthlyIncome)}</td>
                    <td>{app.creditScore}</td>
                    <td>{formatCurrency(app.loanAmount)}</td>
                    <td>
                      {app.decision === "ELIGIBLE" && (
                        <span className="status-eligible">&#10003; ELIGIBLE</span>
                      )}
                      {app.decision === "NOT_ELIGIBLE" && (
                        <span className="status-rejected">&#10007; NOT ELIGIBLE</span>
                      )}
                      {app.decision === "REVIEW_REQUIRED" && (
                        <span className="status-review">&#9888; REVIEW</span>
                      )}
                      {app.decision !== "ELIGIBLE" &&
                        app.decision !== "NOT_ELIGIBLE" &&
                        app.decision !== "REVIEW_REQUIRED" &&
                        app.decision}
                    </td>
                    <td className="small-text">{app.createdAt}</td>
                    <td>
                      <a
                        href="#"
                        onClick={(e) => {
                          e.preventDefault();
                          openDetail(app);
                        }}
                      >
                        View
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          <div className="button-row">
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                newApplication();
              }}
              className="btn-secondary"
            >
              &#171; Back to Application Form
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}

function DetailView({
  application,
  setView,
  newApplication
}: {
  application: LoanApplication;
  setView: (view: View) => void;
  newApplication: () => void;
}) {
  return (
    <div id="page-wrapper">
      <div id="main-content">
        <div className="section-title">DEMO SQL RECORD DETAIL - ID: {application.id}</div>

        <div className="section-content">
          <table className="form-table">
            <tbody>
              <tr>
                <td colSpan={2} style={{ paddingBottom: "8px" }}>
                  <strong>Reference:</strong> {application.applicationReference}
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                  <strong>Submitted:</strong> {application.createdAt}
                </td>
              </tr>
              <tr>
                <td colSpan={2}>
                  <hr style={{ border: "none", borderTop: "1px solid #ddd" }} />
                </td>
              </tr>

              <tr>
                <td className="label-col">Customer Name:</td>
                <td>{application.customerName}</td>
              </tr>
              <tr>
                <td className="label-col">Age:</td>
                <td>{application.age} years</td>
              </tr>
              <tr>
                <td className="label-col">Employment Type:</td>
                <td>{employmentLabel(application.employmentType)}</td>
              </tr>
              <tr>
                <td className="label-col">Monthly Income:</td>
                <td>&#8377;{formatCurrency(application.monthlyIncome)}</td>
              </tr>
              <tr>
                <td className="label-col">Credit Score:</td>
                <td><strong>{application.creditScore}</strong></td>
              </tr>
              <tr>
                <td className="label-col">Existing EMI:</td>
                <td>&#8377;{formatCurrency(application.existingEmi)} / month</td>
              </tr>
              <tr>
                <td className="label-col">Loan Amount Requested:</td>
                <td><strong>&#8377;{formatCurrency(application.loanAmount)}</strong></td>
              </tr>
              <tr>
                <td className="label-col">Loan Tenure:</td>
                <td>{application.loanTenure} months</td>
              </tr>

              <tr>
                <td colSpan={2}>
                  <hr style={{ border: "none", borderTop: "1px solid #ddd", margin: "10px 0" }} />
                </td>
              </tr>

              <tr>
                <td className="label-col"><strong>Decision:</strong></td>
                <td>
                  {application.decision === "ELIGIBLE" && (
                    <span className="status-eligible" style={{ fontSize: "14px" }}>
                      &#10003; ELIGIBLE FOR LOAN
                    </span>
                  )}
                  {application.decision === "NOT_ELIGIBLE" && (
                    <span className="status-rejected" style={{ fontSize: "14px" }}>
                      &#10007; NOT ELIGIBLE
                    </span>
                  )}
                  {application.decision === "REVIEW_REQUIRED" && (
                    <span className="status-review" style={{ fontSize: "14px" }}>
                      &#9888; MORE INFORMATION REQUIRED
                    </span>
                  )}
                </td>
              </tr>
              <tr>
                <td className="label-col">Decision Reason:</td>
                <td><em>{application.decisionReason}</em></td>
              </tr>
            </tbody>
          </table>

          <div className="button-row">
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                setView("applications");
              }}
              className="btn-secondary"
            >
              &#171; Back to List
            </a>
            &nbsp;&nbsp;
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                newApplication();
              }}
              className="btn-secondary"
            >
              New Application
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}
