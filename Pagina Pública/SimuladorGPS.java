import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SimuladorGPS {
    public static void main(String[] args) {
        // URL da API REST do Firebase Realtime Database
        // NOTA: É obrigatório adicionar o ".json" no fim do caminho para o Firebase aceitar pedidos HTTP
        String urlFirebase = "https://onibusnaestrada-ad313-default-rtdb.firebaseio.com/cidades_rotas/Guarabira/rotas/Rota%20do%20Quartel/gps.json";

        // Coordenadas geográficas sequenciais simulando o trajeto do autocarro
        double[][] rotaSimulada = {
            {-6.854500, -35.491200},
            {-6.855200, -35.492100},
            {-6.856100, -35.493300},
            {-6.857300, -35.494500},
            {-6.858800, -35.495800},
            {-6.860100, -35.497100},
            {-6.861500, -35.498400},
            {-6.863000, -35.499900}
        };

        System.out.println("=========================================");
        System.out.println("   SIMULADOR DE GPS EM JAVA INICIADO     ");
        System.out.println("=========================================");
        System.out.println("A transmitir coordenadas para o Firebase...\n");

        try {
            int ponto = 1;
            for (double[] coordenadas : rotaSimulada) {
                double lat = coordenadas[0];
                double lng = coordenadas[1];
                String horaAtual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                // Monta o JSON formatado. Usamos o Locale.US para garantir que os decimais usam ponto (.) e não vírgula
                String jsonConteudo = String.format(Locale.US,
                    "{\"latitude\": %.6f, \"longitude\": %.6f, \"ultimaAtualizacao\": \"%s\"}",
                    lat, lng, horaAtual
                );

                // Abre a ligação HTTP com o Firebase
                URL url = new URL(urlFirebase);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("PUT"); // O método PUT substitui os dados antigos do sensor GPS
                conexao.setRequestProperty("Content-Type", "application/json; utf-8");
                conexao.setDoOutput(true);

                // Envia o JSON com as coordenadas
                try (OutputStream os = conexao.getOutputStream()) {
                    byte[] input = jsonConteudo.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Verifica se o Firebase aceitou o pedido (Código 200 OK)
                int codigoResposta = conexao.getResponseCode();
                if (codigoResposta == 200 || codigoResposta == 201) {
                    System.out.printf("[%s] Ponto %d enviado: Lat %.6f | Lng %.6f %n", 
                                      horaAtual, ponto++, lat, lng);
                } else {
                    System.out.println("⚠️ Erro ao atualizar o Firebase. Código HTTP: " + codigoResposta);
                }

                conexao.disconnect();

                // Aguarda 4 segundos antes de enviar a próxima localização do trajeto
                Thread.sleep(4000);
            }

            System.out.println("\n=========================================");
            System.out.println("Simulação Concluída! O autocarro chegou ao fim.");
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("Ocorreu um erro na execução do Java: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
