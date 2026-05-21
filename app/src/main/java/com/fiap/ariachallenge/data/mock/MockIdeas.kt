package com.fiap.ariachallenge.data.mock

import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import java.time.LocalDateTime

object MockIdeas {

    val idea1 = Idea(
        id = "i1",
        title = "Sistema de gamificação para motoristas",
        author = MockUsers.operador1,
        category = IdeaCategory.TECNOLOGIA,
        description = "Criar um sistema de pontos e recompensas para incentivar direção segura e eficiente entre os motoristas da frota.",
        problema = "Atualmente não há incentivos para motoristas melhorarem performance de direção e consumo de combustível.",
        beneficios = "Redução de acidentes em 30%, economia de combustível estimada em 15%, maior motivação e retenção de motoristas.",
        recursos = "App móvel para motoristas, sistema de tracking GPS, plataforma de gestão de pontos, integração com RH.",
        status = IdeaStatus.EM_ANALISE,
        score = 78,
        createdAt = LocalDateTime.now().minusDays(3),
        updatedAt = LocalDateTime.now().minusHours(2),
        estimatedRoi = 850000.0
    )

    val idea2 = Idea(
        id = "i2",
        title = "App de checklist digital para manutenção",
        author = MockUsers.operador2,
        category = IdeaCategory.PROCESSO,
        description = "Digitalizar todos os checklists de manutenção preventiva e corretiva, eliminando papel e centralizando histórico.",
        problema = "Checklists em papel são perdidos, difíceis de rastrear e não geram dados para análise de tendências.",
        beneficios = "Rastreabilidade completa, histórico digital, alertas automáticos, redução de retrabalho.",
        recursos = "Tablets para mecânicos, integração com sistema de manutenção existente, treinamento da equipe.",
        status = IdeaStatus.APROVADA,
        score = 82,
        createdAt = LocalDateTime.now().minusDays(7),
        updatedAt = LocalDateTime.now().minusDays(1),
        gestorFeedback = "Excelente proposta. Aderente ao plano de digitalização da empresa. Aprovada para projeto piloto.",
        estimatedRoi = 420000.0
    )

    val idea3 = Idea(
        id = "i3",
        title = "Rota inteligente baseada em tráfego em tempo real",
        author = MockUsers.operador1,
        category = IdeaCategory.LOGISTICA,
        description = "Integrar dados de tráfego em tempo real ao sistema de roteirização para otimizar entregas e reduzir tempo em trânsito.",
        problema = "Motoristas perdem tempo em congestionamentos previsíveis que poderiam ser evitados com informações em tempo real.",
        beneficios = "Redução de 20% no tempo de entrega, economia de combustível, maior satisfação do cliente.",
        recursos = "API de mapas e tráfego (Google Maps Platform), integração com sistema de rastreamento.",
        status = IdeaStatus.EM_PROJETO,
        score = 91,
        createdAt = LocalDateTime.now().minusDays(14),
        updatedAt = LocalDateTime.now().minusDays(2),
        gestorFeedback = "Score máximo. Ideia transformadora. Projeto já iniciado.",
        estimatedRoi = 1200000.0
    )

    val idea4 = Idea(
        id = "i4",
        title = "Programa de mentoria entre motoristas sêniores",
        author = MockUsers.operador2,
        category = IdeaCategory.PESSOAS,
        description = "Criar um programa estruturado onde motoristas com mais de 10 anos de empresa orientam os novos contratados.",
        problema = "Alta rotatividade de motoristas novos nos primeiros 6 meses por falta de suporte e integração.",
        beneficios = "Redução de rotatividade, transferência de conhecimento, melhora no clima organizacional.",
        recursos = "Material de treinamento, horas dedicadas dos mentores, certificação interna.",
        status = IdeaStatus.AGUARDANDO_ANALISE,
        createdAt = LocalDateTime.now().minusDays(1),
        updatedAt = LocalDateTime.now().minusDays(1)
    )

    val idea5 = Idea(
        id = "i5",
        title = "Energia solar nos terminais logísticos",
        author = MockUsers.operador3,
        category = IdeaCategory.SUSTENTABILIDADE,
        description = "Instalar painéis solares nos telhados dos terminais logísticos para reduzir consumo de energia elétrica.",
        problema = "Alto custo com energia elétrica nos terminais e pressão regulatória por redução de emissões.",
        beneficios = "Redução de 40% na conta de energia, créditos de carbono, melhor imagem ambiental da empresa.",
        recursos = "Investimento inicial em painéis solares, empresa especializada em instalação.",
        status = IdeaStatus.APROVADA,
        score = 88,
        createdAt = LocalDateTime.now().minusDays(10),
        updatedAt = LocalDateTime.now().minusDays(3),
        gestorFeedback = "Proposta sólida com retorno garantido. Aprovada.",
        estimatedRoi = 2100000.0
    )

    val idea6 = Idea(
        id = "i6",
        title = "Sistema de feedback pós-entrega via WhatsApp",
        author = MockUsers.operador4,
        category = IdeaCategory.ATENDIMENTO,
        description = "Envio automático de mensagem de WhatsApp após cada entrega solicitando avaliação do serviço.",
        problema = "Taxa de NPS atual muito baixa por dificuldade no processo de coleta de feedback.",
        beneficios = "Maior volume de feedbacks, resposta rápida a problemas, melhora no NPS.",
        recursos = "API do WhatsApp Business, integração com sistema de rastreamento de entregas.",
        status = IdeaStatus.REJEITADA,
        score = 45,
        createdAt = LocalDateTime.now().minusDays(5),
        updatedAt = LocalDateTime.now().minusDays(2),
        gestorFeedback = "Boa iniciativa, mas existem questões de LGPD que precisam ser endereçadas antes. Resubmeta com plano de conformidade."
    )

    val idea7 = Idea(
        id = "i7",
        title = "Gestão preditiva de manutenção com IoT",
        author = MockUsers.operador2,
        category = IdeaCategory.TECNOLOGIA,
        description = "Instalar sensores IoT nos veículos para monitorar em tempo real parâmetros críticos e prever falhas antes que ocorram.",
        problema = "Paradas não planejadas de veículos geram prejuízo médio de R$15.000 por ocorrência.",
        beneficios = "Redução de 70% em paradas não planejadas, maior vida útil dos veículos, previsibilidade de custo.",
        recursos = "Sensores IoT, plataforma de análise de dados, integração com oficinas.",
        status = IdeaStatus.EM_ANALISE,
        score = 85,
        createdAt = LocalDateTime.now().minusDays(4),
        updatedAt = LocalDateTime.now().minusHours(12),
        estimatedRoi = 3200000.0
    )

    val idea8 = Idea(
        id = "i8",
        title = "Troca de documentação física por e-DOC",
        author = MockUsers.operador3,
        category = IdeaCategory.PROCESSO,
        description = "Digitalizar todos os documentos de carga e transporte, eliminando a necessidade de documentação física.",
        problema = "Perda de documentos, custo com impressão e armazenamento, atrasos por problemas com documentação.",
        beneficios = "Economia de R$50.000/ano em papel, agilidade, rastreabilidade.",
        recursos = "Sistema de gestão documental, assinatura digital, treinamento.",
        status = IdeaStatus.AGUARDANDO_ANALISE,
        createdAt = LocalDateTime.now().minusHours(6),
        updatedAt = LocalDateTime.now().minusHours(6)
    )

    val idea9 = Idea(
        id = "i9",
        title = "Câmeras de IA para detecção de fadiga",
        author = MockUsers.operador1,
        category = IdeaCategory.SEGURANCA,
        description = "Instalar câmeras com inteligência artificial nos veículos para detectar sinais de fadiga dos motoristas em tempo real.",
        problema = "Acidentes causados por fadiga representam 30% dos acidentes graves na empresa.",
        beneficios = "Prevenção de acidentes, redução de multas e processos, proteção da vida dos motoristas.",
        recursos = "Câmeras com processamento de IA, alertas sonoros, central de monitoramento.",
        status = IdeaStatus.EM_PROJETO,
        score = 95,
        createdAt = LocalDateTime.now().minusDays(20),
        updatedAt = LocalDateTime.now().minusDays(5),
        estimatedRoi = 5000000.0
    )

    val idea10 = Idea(
        id = "i10",
        title = "Parceria com academias para bem-estar dos motoristas",
        author = MockUsers.operador4,
        category = IdeaCategory.PESSOAS,
        description = "Negociar convênio com rede de academias para oferecer benefício de saúde e bem-estar aos motoristas.",
        problema = "Alto absenteísmo por problemas de saúde relacionados à vida sedentária dos motoristas.",
        beneficios = "Redução de 25% no absenteísmo, maior satisfação, imagem de empregador responsável.",
        recursos = "Negociação com academia, subsídio parcial pela empresa.",
        status = IdeaStatus.AGUARDANDO_ANALISE,
        createdAt = LocalDateTime.now().minusHours(18),
        updatedAt = LocalDateTime.now().minusHours(18)
    )

    val allIdeas = listOf(idea1, idea2, idea3, idea4, idea5, idea6, idea7, idea8, idea9, idea10)

    fun getByStatus(status: IdeaStatus) = allIdeas.filter { it.status == status }
    fun getByAuthor(authorId: String) = allIdeas.filter { it.author.id == authorId }
    fun getById(id: String) = allIdeas.find { it.id == id }
    fun getPending() = allIdeas.filter {
        it.status == IdeaStatus.AGUARDANDO_ANALISE || it.status == IdeaStatus.EM_ANALISE
    }

    fun getSimilarIdeas(excludeAuthorId: String = ""): List<Idea> =
        allIdeas.filter { it.author.id != excludeAuthorId && it.score != null }
            .sortedByDescending { it.score }
            .take(3)
}
