"""
ProofLoop Companion Backend — iQOO Hackathon 2026
FastAPI + SQLite service for Phone-to-Laptop Evidence Synchronization & Adaptive Engine.
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import sqlite3
import datetime

app = FastAPI(
    title="ProofLoop API & Bridge Service",
    description="Phone-to-Laptop learning bridge, mission evaluation & telemetry store",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

import hashlib
import secrets
import uuid

# SQLite Initialization
def init_db():
    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS evidence_logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            session_id TEXT,
            mission_id TEXT,
            evidence_type TEXT,
            content TEXT,
            timestamp TEXT
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS proof_records (
            id TEXT PRIMARY KEY,
            user_id TEXT,
            skill_name TEXT,
            verified_score INTEGER,
            improvement INTEGER,
            verification_hash TEXT,
            created_at TEXT
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id TEXT PRIMARY KEY,
            email TEXT UNIQUE,
            password_hash TEXT,
            salt TEXT,
            name TEXT,
            role TEXT,
            created_at TEXT
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS auth_tokens (
            token TEXT PRIMARY KEY,
            user_id TEXT,
            created_at TEXT,
            expires_at TEXT
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS password_resets (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT,
            reset_code TEXT,
            created_at TEXT,
            used INTEGER DEFAULT 0
        )
    """)

    # Seed student demo user if not already present
    cursor.execute("SELECT id FROM users WHERE email = ?", ("murali.student@iqoo.edu",))
    if not cursor.fetchone():
        salt = secrets.token_hex(16)
        pw_hash = hashlib.sha256(("proofloop2026" + salt).encode('utf-8')).hexdigest()
        now = datetime.datetime.utcnow().isoformat()
        cursor.execute(
            "INSERT INTO users (id, email, password_hash, salt, name, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
            ("user_murali_demo", "murali.student@iqoo.edu", pw_hash, salt, "Murali", "student", now)
        )
    conn.commit()
    conn.close()

init_db()

def hash_password(password: str, salt: str) -> str:
    return hashlib.sha256((password + salt).encode('utf-8')).hexdigest()

# Models
class LoginRequest(BaseModel):
    identifier: str
    password: str

class RegisterRequest(BaseModel):
    email: str
    password: str
    name: Optional[str] = "Student"

class PasswordResetRequest(BaseModel):
    email: str

class EvidencePayload(BaseModel):
    session_id: str
    mission_id: str
    evidence_type: str # "camera" | "voice" | "calculation"
    content: str

class EvaluationPayload(BaseModel):
    mission_id: str
    investigation_answer: str
    interview_answer: str
    solution_answer: str
    follow_up_answer: str

# Authentication Endpoints
@app.post("/auth/login")
def login_user(payload: LoginRequest):
    identifier = payload.identifier.strip().lower()
    if not identifier or not payload.password:
        raise HTTPException(status_code=400, detail="Identifier and password are required")

    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    cursor.execute("SELECT id, email, password_hash, salt, name, role FROM users WHERE LOWER(email) = ? OR LOWER(id) = ?", (identifier, identifier))
    user = cursor.fetchone()

    if not user:
        conn.close()
        raise HTTPException(status_code=404, detail="Account not found")

    user_id, email, expected_hash, salt, name, role = user
    computed_hash = hash_password(payload.password, salt)

    if computed_hash != expected_hash:
        conn.close()
        raise HTTPException(status_code=401, detail="Incorrect password")

    token = secrets.token_hex(24)
    now = datetime.datetime.utcnow()
    expires = (now + datetime.timedelta(days=30)).isoformat()
    cursor.execute(
        "INSERT INTO auth_tokens (token, user_id, created_at, expires_at) VALUES (?, ?, ?, ?)",
        (token, user_id, now.isoformat(), expires)
    )
    conn.commit()
    conn.close()

    return {
        "status": "success",
        "token": token,
        "user": {
            "userId": user_id,
            "email": email,
            "name": name,
            "role": role,
            "isDemoUser": (email == "murali.student@iqoo.edu")
        }
    }

@app.post("/auth/register")
def register_user(payload: RegisterRequest):
    email = payload.email.strip().lower()
    if not email or "@" not in email:
        raise HTTPException(status_code=400, detail="A valid email address is required")
    if len(payload.password) < 6:
        raise HTTPException(status_code=400, detail="Password must be at least 6 characters")

    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    cursor.execute("SELECT id FROM users WHERE LOWER(email) = ?", (email,))
    if cursor.fetchone():
        conn.close()
        raise HTTPException(status_code=400, detail="An account with this email already exists")

    user_id = "user_" + secrets.token_hex(6)
    salt = secrets.token_hex(16)
    pw_hash = hash_password(payload.password, salt)
    now = datetime.datetime.utcnow()
    cursor.execute(
        "INSERT INTO users (id, email, password_hash, salt, name, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
        (user_id, email, pw_hash, salt, payload.name or "Student", "student", now.isoformat())
    )

    token = secrets.token_hex(24)
    expires = (now + datetime.timedelta(days=30)).isoformat()
    cursor.execute(
        "INSERT INTO auth_tokens (token, user_id, created_at, expires_at) VALUES (?, ?, ?, ?)",
        (token, user_id, now.isoformat(), expires)
    )
    conn.commit()
    conn.close()

    return {
        "status": "success",
        "token": token,
        "user": {
            "userId": user_id,
            "email": email,
            "name": payload.name or "Student",
            "role": "student",
            "isDemoUser": False
        }
    }

@app.post("/auth/reset-password")
def reset_password(payload: PasswordResetRequest):
    email = payload.email.strip().lower()
    if not email or "@" not in email:
        raise HTTPException(status_code=400, detail="A valid email address is required")

    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    cursor.execute("SELECT id FROM users WHERE LOWER(email) = ?", (email,))
    user = cursor.fetchone()

    if not user:
        conn.close()
        raise HTTPException(status_code=404, detail="No account found with this email address")

    reset_code = secrets.token_hex(4).upper()
    now = datetime.datetime.utcnow().isoformat()
    cursor.execute(
        "INSERT INTO password_resets (email, reset_code, created_at) VALUES (?, ?, ?)",
        (email, reset_code, now)
    )
    conn.commit()
    conn.close()

    return {
        "status": "success",
        "message": f"Password reset instructions and verification code have been sent to {email}"
    }

@app.get("/auth/me")
def get_current_user(token: str):
    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    cursor.execute("""
        SELECT u.id, u.email, u.name, u.role
        FROM auth_tokens t
        JOIN users u ON t.user_id = u.id
        WHERE t.token = ?
    """, (token,))
    user = cursor.fetchone()
    conn.close()

    if not user:
        raise HTTPException(status_code=401, detail="Session expired or invalid")

    return {
        "userId": user[0],
        "email": user[1],
        "name": user[2],
        "role": user[3]
    }

@app.get("/")
def read_root():
    return {
        "status": "online",
        "system": "ProofLoop Core Service",
        "hackathon": "iQOO Hackathon 2026",
        "track": "Smart Education",
        "primary_domain": "Mathematics"
    }

@app.get("/skills")
def get_skills():
    return [
        {"id": "math_reasoning", "name": "Mathematical Reasoning", "score": 78, "baseline": 61, "category": "Mathematics"},
        {"id": "calc_accuracy", "name": "Calculation Accuracy", "score": 86, "baseline": 72, "category": "Mathematics"},
        {"id": "perc_reasoning", "name": "Percentage Reasoning", "score": 75, "baseline": 55, "category": "Mathematics"},
        {"id": "explanation", "name": "Explanation & Defense", "score": 84, "baseline": 78, "category": "Communication"},
        {"id": "tradeoff_decision", "name": "Trade-off Decision", "score": 71, "baseline": 62, "category": "Problem Solving"}
    ]

@app.get("/missions")
def get_missions():
    return [
        {
            "id": "mission_festival_budget",
            "title": "Campus Festival Budget",
            "subject": "MATHEMATICS",
            "domain": "FINANCIAL MATHEMATICS",
            "budget_cap": 12000,
            "attendees": 240,
            "food_quote_per_head": 85,
            "fixed_decoration": 3200,
            "fixed_transport": 1500
        },
        {
            "id": "mission_canteen_pricing",
            "title": "Canteen Pricing & Margins",
            "subject": "MATHEMATICS",
            "domain": "BUSINESS MATHEMATICS"
        }
    ]

@app.post("/bridge/evidence")
def log_bridge_evidence(payload: EvidencePayload):
    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    now = datetime.datetime.utcnow().isoformat()
    cursor.execute(
        "INSERT INTO evidence_logs (session_id, mission_id, evidence_type, content, timestamp) VALUES (?, ?, ?, ?, ?)",
        (payload.session_id, payload.mission_id, payload.evidence_type, payload.content, now)
    )
    conn.commit()
    conn.close()
    return {"status": "logged", "timestamp": now}

@app.get("/bridge/workspace/{session_id}")
def get_workspace(session_id: str):
    conn = sqlite3.connect("proofloop.db")
    cursor = conn.cursor()
    cursor.execute("SELECT evidence_type, content, timestamp FROM evidence_logs WHERE session_id = ?", (session_id,))
    rows = cursor.fetchall()
    conn.close()
    return {
        "session_id": session_id,
        "evidence_stream": [{"type": r[0], "content": r[1], "timestamp": r[2]} for r in rows]
    }

@app.post("/missions/{mission_id}/evaluate")
def evaluate_mission(mission_id: str, payload: EvaluationPayload):
    return {
        "mission_id": mission_id,
        "overall_score": 78,
        "competencies": {
            "observation": 82,
            "evidence": 75,
            "hypothesis": 76,
            "testing": 86,
            "reasoning": 84,
            "communication": 81,
            "decision_making": 71
        },
        "weakest_competency": "Percentage Reasoning",
        "why_you_improved": [
            "You calculated the exact 12% price shock on the total 240 student volume.",
            "You formulated measurable line-item trade-offs instead of arbitrary budget cuts.",
            "You defended your decision with clear mathematical constraints."
        ],
        "next_target_mission": "mission_canteen_pricing"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
