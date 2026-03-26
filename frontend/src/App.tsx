import { useEffect, useState } from "react";
import type { CSSProperties } from "react";
import "./App.css";

type DecisionReason = "DEBT" | "NO_VALID_LOAN" | "UNSUPPORTED_PERSONAL_CODE";

type DecisionResponse = {
    approved: boolean;
    approvedAmount: number | null;
    approvedPeriod: number | null;
    errorMessage: string | null;
    reason: DecisionReason | null;
};

const AMOUNT_MIN = 2000;
const AMOUNT_MAX = 10000;
const PERIOD_MIN = 12;
const PERIOD_MAX = 60;
const PERIOD_STEP = 1;
const AMOUNT_STEP = 100;

function getRangeStyle(value: number, min: number, max: number): CSSProperties {
    const progress = ((value - min) / (max - min)) * 100;

    return {
        ["--range-progress" as string]: `${progress}%`,
    };
}

function App() {
    const [personalCode, setPersonalCode] = useState("");
    const [loanAmount, setLoanAmount] = useState(4000);
    const [loanPeriod, setLoanPeriod] = useState(24);

    const [result, setResult] = useState<DecisionResponse | null>(null);
    const [requestError, setRequestError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const isPersonalCodeEmpty = personalCode.length === 0;
    const isPersonalCodeIncomplete = personalCode.length > 0 && personalCode.length < 11;
    const isUnsupportedPersonalCode = result?.reason === "UNSUPPORTED_PERSONAL_CODE";
    const hasDebt = result?.reason === "DEBT";

    const showPersonalCodeError =
        isPersonalCodeEmpty || isPersonalCodeIncomplete || isUnsupportedPersonalCode;

    useEffect(() => {
        const isValidFormat = /^\d{11}$/.test(personalCode);

        if (!isValidFormat) {
            setResult(null);
            setRequestError(null);
            setLoading(false);
            return;
        }

        const controller = new AbortController();

        const timeoutId = setTimeout(async () => {
            setLoading(true);
            setRequestError(null);

            try {
                const response = await fetch("/api/decision", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        personalCode,
                        loanAmount,
                        loanPeriod,
                    }),
                    signal: controller.signal,
                });

                const data: DecisionResponse = await response.json();
                setResult(data);
                setRequestError(null);
            } catch (error) {
                if (error instanceof DOMException && error.name === "AbortError") {
                    return;
                }

                setRequestError("Could not connect to backend");
                setResult(null);
            } finally {
                setLoading(false);
            }
        }, 350);

        return () => {
            clearTimeout(timeoutId);
            controller.abort();
        };
    }, [personalCode, loanAmount, loanPeriod]);

    const handlePersonalCodeChange = (value: string) => {
        const digitsOnly = value.replace(/\D/g, "").slice(0, 11);
        setPersonalCode(digitsOnly);
    };

    return (
        <main className="page">
            <div className="calculator-card">
                <div className="card-header">
                    <h1 className="card-title">Calculate your maximum loan offer</h1>
                </div>

                <div className="field-group">
                    <label className={`field-box ${showPersonalCodeError ? "field-box-error" : ""}`}>
                        <span
                            className={`floating-label ${
                                showPersonalCodeError ? "floating-label-error" : ""
                            }`}
                        >
                            Personal code
                        </span>
                        <input
                            className={`text-input ${showPersonalCodeError ? "text-input-error" : ""}`}
                            type="text"
                            inputMode="numeric"
                            autoComplete="off"
                            value={personalCode}
                            onChange={(e) => handlePersonalCodeChange(e.target.value)}
                            placeholder=""
                            aria-invalid={showPersonalCodeError}
                        />
                    </label>

                    {isPersonalCodeEmpty ? (
                        <p className="helper-text helper-text-error">
                            Please enter your personal code.
                        </p>
                    ) : isPersonalCodeIncomplete ? (
                        <p className="helper-text helper-text-error">
                            Personal code must be 11 digits long.
                        </p>
                    ) : isUnsupportedPersonalCode ? (
                        <p className="helper-text helper-text-error">
                            Unsupported personal code.
                        </p>
                    ) : null}

                    <p className="helper-text helper-text-secondary">
                        Demo codes: 49002010965, 49002010976, 49002010987, or 49002010998
                    </p>
                </div>

                <div className="slider-block">
                    <p id="loan-amount-label" className="slider-title">
                        Loan amount: {loanAmount} €
                    </p>
                    <input
                        id="loan-amount"
                        className="range-input"
                        type="range"
                        min={AMOUNT_MIN}
                        max={AMOUNT_MAX}
                        step={AMOUNT_STEP}
                        value={loanAmount}
                        onChange={(e) => setLoanAmount(Number(e.target.value))}
                        style={getRangeStyle(loanAmount, AMOUNT_MIN, AMOUNT_MAX)}
                        aria-labelledby="loan-amount-label"
                        aria-valuemin={AMOUNT_MIN}
                        aria-valuemax={AMOUNT_MAX}
                        aria-valuenow={loanAmount}
                        aria-valuetext={`${loanAmount} euros`}
                    />
                    <div className="range-labels" aria-hidden="true">
                        <span>{AMOUNT_MIN} €</span>
                        <span>{AMOUNT_MAX} €</span>
                    </div>
                </div>

                <div className="slider-block">
                    <p id="loan-period-label" className="slider-title">
                        Loan period: {loanPeriod} months
                    </p>
                    <input
                        id="loan-period"
                        className="range-input"
                        type="range"
                        min={PERIOD_MIN}
                        max={PERIOD_MAX}
                        step={PERIOD_STEP}
                        value={loanPeriod}
                        onChange={(e) => setLoanPeriod(Number(e.target.value))}
                        style={getRangeStyle(loanPeriod, PERIOD_MIN, PERIOD_MAX)}
                        aria-labelledby="loan-period-label"
                        aria-valuemin={PERIOD_MIN}
                        aria-valuemax={PERIOD_MAX}
                        aria-valuenow={loanPeriod}
                        aria-valuetext={`${loanPeriod} months`}
                    />
                    <div className="range-labels" aria-hidden="true">
                        <span>{PERIOD_MIN} months</span>
                        <span>{PERIOD_MAX} months</span>
                    </div>
                </div>

                <div className="result-section" aria-live="polite" aria-busy={loading}>
                    {hasDebt ? (
                        <div className="debt-result-box">
                            <p className="debt-result-message">
                                We are currently unable to offer loans to applicants with active debt.
                            </p>
                        </div>
                    ) : (
                        <>
                            <p className="result-label">Maximum available offer</p>
                            <h2 className="result-value">
                                {loading
                                    ? "..."
                                    : result?.approvedAmount != null
                                        ? `${result.approvedAmount} €`
                                        : "-- €"}
                            </h2>

                            <p className="result-label second-label">Offer period</p>
                            <h3 className="result-period">
                                {loading
                                    ? "..."
                                    : result?.approvedPeriod != null
                                        ? `${result.approvedPeriod} months`
                                        : "-- months"}
                            </h3>

                            {result &&
                                !result.approved &&
                                result.errorMessage &&
                                !hasDebt &&
                                !isUnsupportedPersonalCode && (
                                    <p className="inline-message">{result.errorMessage}</p>
                                )}
                        </>
                    )}

                    {requestError && (
                        <p className="inline-message" role="alert">
                            {requestError}
                        </p>
                    )}
                </div>
            </div>
        </main>
    );
}

export default App;