from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from app.database import connect_to_mongo, close_mongo_connection
import logging
from app.routers import templates

logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    await connect_to_mongo()
    print("✅ Serviços inicializados")
    yield
    # Shutdown
    await close_mongo_connection()
    print("❌ Serviços encerrados")

app = FastAPI(
    title="Solo RPG API",
    description="API para gerenciamento de templates de fichas de RPG",
    lifespan=lifespan
)


app.include_router(templates.router, prefix="/api")

@app.get("/")
async def health_check():
    return {"status": "online"}