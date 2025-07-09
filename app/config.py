from pydantic_settings import BaseSettings
from pathlib import Path

class Settings(BaseSettings):
    # Configurações padrão (podem ser sobrescritas pelo .env)
    MONGODB_URL: str = "mongodb://localhost:27017"
    MONGODB_NAME: str = "sheet-models"
    API_PORT: int = 7000
    
    class Config:
        # Permite carregar de um arquivo .env
        env_file = Path(__file__).parent / ".env"
        env_file_encoding = 'utf-8'

settings = Settings()