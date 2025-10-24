# sheet-templates/app/__init__.py

# Exportações explícitas (opcional)
from .database import db, connect_to_mongo, close_mongo_connection
from .models import TemplateCreate, TemplateResponse

__all__ = [
    'db',
    'connect_to_mongo',
    'close_mongo_connection',
    'TemplateCreate',
    'TemplateResponse'
]