from fastapi import APIRouter, Body, HTTPException, status
from app.models import TemplateCreate, TemplateResponse, TemplateUpdate, TemplateField
from app.database import get_database
from pymongo.errors import DuplicateKeyError
from bson import ObjectId
from datetime import datetime
from pydantic import ValidationError

router = APIRouter(
    tags=["Templates"],  # Adiciona tag para organização no Swagger UI
)

@router.get(
    "/by-name/{system_name}",
    summary="Buscar template por nome do sistema",
    response_model=TemplateResponse,
    responses={
        404: {"description": "Template não encontrado"},
        500: {"description": "Erro interno no servidor"}
    }
)
async def get_template_by_name(system_name: str):
    try:
        db = get_database()
        template = await db.templates.find_one({"system_name": system_name})
        
        if not template:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Template com nome '{system_name}' não encontrado"
            )
            
        return TemplateResponse(
            id=str(template["_id"]),
            system_name=template["system_name"],
            version=template["version"],
            fields=template["fields"],
            template_json=template.get("template_json")
        )
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao buscar template por nome: {str(e)}"
        )

@router.get(
    "/by-id/{system_id}",
    summary="Buscar template por ID",
    response_model=TemplateResponse,
    responses={
        400: {"description": "ID inválido"},
        404: {"description": "Template não encontrado"},
        500: {"description": "Erro interno no servidor"}
    }
)
async def get_template_by_id(system_id: str):
    try:
        # Validação do ObjectId
        if not ObjectId.is_valid(system_id):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="ID do template inválido"
            )

        db = get_database()
        template = await db.templates.find_one({"_id": ObjectId(system_id)})
        
        if not template:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Template com ID '{system_id}' não encontrado"
            )
            
        return TemplateResponse(
            id=str(template["_id"]),
            system_name=template["system_name"],
            version=template["version"],
            fields=template["fields"],
            template_json=template.get("template_json")
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao buscar template por ID: {str(e)}"
        )

@router.get("/{system_name}/fields")
async def get_template_fields(system_name: str):
    db = get_database()
    # Busca o template pelo nome do sistema
    template = await db.templates.find_one({"system_name": system_name})
    if not template:
        raise HTTPException(404, detail="Template não encontrado")
    
    return {
        # Retorna os campos do template
        "id": str(template["_id"]),
        "system_name": template["system_name"],
        "version": template["version"],
        "fields": template["fields"],
    }

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
async def create_template(template_data: TemplateCreate  = Body(
        ...,
        example={
            "system_name": "D&D 5e",
            "version": "1.0",
            "fields": [
                {
                    "name": "nome",
                    "type": "string",
                    "required": True,
                    "default": "Novo Personagem"
                },
                {
                    "name": "classe",
                    "type": "string",
                    "required": True,
                    "default": "guerreiro",
                    "options": ["guerreiro", "mago", "ladino"]
                },
                {
                    "name": "atributos",
                    "type": "object",
                    "required": True,
                    "fields": [
                        {
                            "name": "força",
                            "type": "object",
                            "required": True,
                            "fields": [
                                {"name": "valor", "type": "number", "required": True, "default": 10},
                                {"name": "bônus", "type": "number", "required": True, "default": 0}
                            ]
                        },
                        {
                            "name": "destreza",
                            "type": "object",
                            "required": True,
                            "fields": [
                                {"name": "valor", "type": "number", "required": True, "default": 10},
                                {"name": "bônus", "type": "number", "required": True, "default": 0}
                            ]
                        }
                    ]
                }
            ],
            "template_json": {
                "blocos": [
                    {"titulo": "Identificação", "campos": ["nome", "classe"]},
                    {"titulo": "Atributos", "campos": ["atributos"]}
                ]
            }
        }
    )
):
    print("📥 Dados recebidos:", template_data)
    try:
        db = get_database()
        print("📦 Conexão com DB:", db)
        
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

@router.delete(
    "/{system_name}",
    summary="Excluir um template pelo nome do sistema",
    responses={
        404: {"description": "Template não encontrado"},
        500: {"description": "Erro interno no servidor"}
    }
)
async def delete_template(system_name: str):
    try:
        db = get_database()
        result = await db.templates.delete_one({"system_name": system_name})
        if result.deleted_count == 0:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Template para sistema {system_name} não encontrado"
            )
    except HTTPException:
        raise  # Re-lança exceções HTTP que já tratamos
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao excluir template: {str(e)}"
        )

@router.delete(
    "/by-id/{system_id}",
    summary="Excluir um template pelo ID",
    responses={
        400: {"description": "ID inválido"},
        404: {"description": "Template não encontrado"},
        500: {"description": "Erro interno no servidor"}
    }
)
async def delete_template_by_id(system_id: str):
    try:
        db = get_database()
        result = await db.templates.delete_one({"_id": ObjectId(system_id)})
        if result.deleted_count == 0:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Template com ID {system_id} não encontrado"
            )
    except HTTPException:
        raise  # Re-lança exceções HTTP que já tratamos
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao excluir template: {str(e)}"
        )

@router.put(
    "/{template_id}",  # Agora usando template_id como parâmetro
    summary="Atualizar um template pelo ID",
    response_model=TemplateResponse,
    responses={
        404: {"description": "Template não encontrado"},
        400: {"description": "Dados inválidos ou duplicados"},
        422: {"description": "Erro de validação"},
        500: {"description": "Erro interno no servidor"}
    }
)
async def update_template(template_id: str, template_data: TemplateUpdate):
    try:
        # Validação do ObjectId
        if not ObjectId.is_valid(template_id):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="ID do template inválido"
            )

        db = get_database()
        
        # Verificar se o template existe
        existing = await db.templates.find_one({"_id": ObjectId(template_id)})
        if not existing:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Template com ID {template_id} não encontrado"
            )
        
        # Atualizar template
        update_data = template_data.model_dump(exclude_unset=True)
        update_data["updated_at"] = datetime.utcnow()
        
        result = await db.templates.update_one(
            {"_id": ObjectId(template_id)},
            {"$set": update_data}
        )
        
        if result.modified_count == 0:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Nenhuma alteração foi realizada"
            )
        
        # Retornar os dados atualizados
        updated_template = {**existing, **update_data}
        updated_template["id"] = str(updated_template["_id"])
        return TemplateResponse(**updated_template)
        
    except ValidationError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail={
                "errors": {
                    field: [error["msg"] for error in errors]
                    for field, errors in e.errors()
                }
            }
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao atualizar template: {str(e)}"
        )