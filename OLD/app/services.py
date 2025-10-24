from app.database import get_database
from app.models import TemplateCreate, TemplateResponse
from bson import ObjectId

async def create_template(template: TemplateCreate) -> TemplateResponse:
    db = get_database()
    result = await db.templates.insert_one(template.dict())
    return TemplateResponse(**template.dict(), id=str(result.inserted_id))

async def get_template(system_name: str) -> Dict:
    db = get_database()
    template = await db.templates.find_one({"system_name": system_name})
    if not template:
        return None
    template["id"] = str(template["_id"])
    return template

async def list_templates() -> List[Dict]:
    db = get_database()
    return [doc async for doc in db.templates.find({})]