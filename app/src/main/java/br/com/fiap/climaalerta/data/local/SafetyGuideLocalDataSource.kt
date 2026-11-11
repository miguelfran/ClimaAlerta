package br.com.fiap.climaalerta.data.local

import br.com.fiap.climaalerta.domain.model.AlertType
import br.com.fiap.climaalerta.domain.model.EmergencyContact
import br.com.fiap.climaalerta.domain.model.SafetyTopic

class SafetyGuideLocalDataSource {

    fun getEmergencyContacts(): List<EmergencyContact> = listOf(
        EmergencyContact("Defesa Civil", "199", "Alagamentos e deslizamentos"),
        EmergencyContact("Bombeiros", "193", "Resgates e quedas de árvores"),
        EmergencyContact("SAMU", "192", "Emergências médicas e insolação"),
        EmergencyContact("Polícia Militar", "190", "Segurança e vias bloqueadas")
    )

    fun getSafetyTopics(): List<SafetyTopic> = listOf(
        SafetyTopic(
            type = AlertType.HEAVY_RAIN,
            title = "Chuvas intensas e alagamentos",
            tips = listOf(
                "Não atravesse ruas alagadas, a pé ou de carro.",
                "Fique atento a rachaduras, estalos e inclinação de postes e árvores em encostas.",
                "Desligue a energia elétrica se a água começar a entrar em casa.",
                "Guarde documentos e remédios em sacos plásticos em local alto."
            )
        ),
        SafetyTopic(
            type = AlertType.STORM,
            title = "Tempestades e raios",
            tips = listOf(
                "Abrigue-se em construções fechadas; carros também protegem contra raios.",
                "Evite campos abertos, praias, piscinas e o uso de aparelhos ligados à tomada.",
                "Não fique embaixo de árvores, marquises ou estruturas metálicas."
            )
        ),
        SafetyTopic(
            type = AlertType.EXTREME_HEAT,
            title = "Calor extremo",
            tips = listOf(
                "Beba água mesmo sem sentir sede e evite bebidas alcoólicas.",
                "Evite atividades ao sol entre 10h e 16h.",
                "Nunca deixe crianças, idosos ou animais dentro de veículos fechados.",
                "Procure atendimento se houver tontura, náusea ou confusão mental."
            )
        ),
        SafetyTopic(
            type = AlertType.STRONG_WIND,
            title = "Ventos fortes",
            tips = listOf(
                "Recolha objetos soltos em varandas, lajes e quintais.",
                "Mantenha distância de árvores, placas, outdoors e fiação elétrica.",
                "Se houver fios caídos, não toque e acione a concessionária ou os Bombeiros."
            )
        ),
        SafetyTopic(
            type = AlertType.INTENSE_COLD,
            title = "Frio intenso",
            tips = listOf(
                "Use roupas em camadas e proteja cabeça, mãos e pés.",
                "Não use fogareiros ou braseiros em ambientes fechados.",
                "Ajude pessoas em situação de rua informando os abrigos da prefeitura (156)."
            )
        ),
        SafetyTopic(
            type = AlertType.LOW_HUMIDITY,
            title = "Tempo seco e baixa umidade",
            tips = listOf(
                "Hidrate-se e use soro fisiológico nos olhos e narinas.",
                "Evite exercícios ao ar livre entre 10h e 17h.",
                "Use umidificadores, toalhas molhadas ou bacias com água nos ambientes."
            )
        )
    )
}
