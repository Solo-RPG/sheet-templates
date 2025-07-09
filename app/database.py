from motor.motor_asyncio import AsyncIOMotorClient
from app.config import settings
import pymongo

class MongoDB:
    client: AsyncIOMotorClient = None
    sync_client: pymongo.MongoClient = None  # Para operações síncronas se precisar

db = MongoDB()

async def connect_to_mongo():
    try:
        db.client = AsyncIOMotorClient(
            settings.MONGODB_URL,
            server_api=pymongo.server_api.ServerApi('1')
        )

        db.sync_client = pymongo.MongoClient(
            settings.MONGODB_URL,
            server_api=pymongo.server_api.ServerApi('1')
        )

        await db.client.admin.command('ping')
        print("✅ Conectado ao MongoDB Atlas!")

        # ⬇️ Aqui adiciona o índice único
        await db.client[settings.MONGODB_NAME].templates.create_index(
            "system_name", unique=True
        )
        print("✅ Índice único em system_name garantido")

    except Exception as e:
        print(f"❌ Falha na conexão: {e}")
        raise


def get_database():
    return db.client[settings.MONGODB_NAME]

async def close_mongo_connection():
    if db.client:
        db.client.close()
        print("❌ Desconectado do MongoDB Atlas")