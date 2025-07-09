# app/routers/templates.py
from fastapi import APIRouter, HTTPException, status
from app.models import TemplateCreate, TemplateResponse
from app.database import get_database
from pymongo.errors import DuplicateKeyError
from bson import ObjectId

router = APIRouter(
    prefix="/templates",
    tags=["Templates"],  # Adiciona tag para organização no Swagger UI
)

@router.get(
    "/{system_name}",
    summary="Buscar template por nome do sistema",
    response_model=TemplateResponse,
    responses={
        404: {"description": "Template não encontrado"},
        500: {"description": "Erro interno no servidor"}
    }
)

async def get_template(system_name: str):
    try:
        db = get_database()
        template = await db.templates.find_one({"system_name": system_name})
        
        if not template:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Template não encontrado"
            )
            
        template["id"] = str(template["_id"])  # Converte ObjectId para string
        return TemplateResponse(
            id=str(template["_id"]),
            system_name=template["system_name"],
            version=template["version"],
            fields=template["fields"],
            template_json=template["template_json"]
        )

        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao buscar template: {str(e)}"
        )

@router.get(
    "/",
    response_model=list[TemplateResponse],
    summary="Lista todos os templates disponíveis"
)
async def list_templates():
    try:
        db = get_database()
        templates = []
        async for template in db.templates.find({}):
            template["id"] = str(template["_id"])
            templates.append(template)
        return templates
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao listar templates: {str(e)}"
        )

@router.post(
    "/",
    summary="Criar um novo template",
    response_model=TemplateResponse,
    status_code=status.HTTP_201_CREATED,
    responses={
        400: {"description": "Dados inválidos ou duplicados"},
        500: {"description": "Erro interno no servidor"}
    }
)
async def create_template(template_data: TemplateCreate):
    try:
        db = get_database()
        
        # Usa model_dump() em vez de dict() para Pydantic v2
        template_dict = template_data.model_dump()
        
        # Verifica se template já existe
        if await db.templates.find_one({"system_name": template_dict["system_name"]}):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Template para sistema {template_dict['system_name']} já existe"
            )
            
        result = await db.templates.insert_one(template_dict)
        
        return TemplateResponse(
            **template_dict,
            id=str(result.inserted_id)
            )
            
    except DuplicateKeyError:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Template com esse nome já existe"
        )
    except HTTPException:
        raise  # Re-lança exceções HTTP que já tratamos
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao criar template: {str(e)}"
        )