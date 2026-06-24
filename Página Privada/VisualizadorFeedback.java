import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisualizadorFeedback {
    public static void main(String[] args) {
        // URL da API REST do Firebase para puxar os feedbacks salvos pelos alunos
        String urlFirebase = "https://onibusnaestrada-ad313-default-rtdb.firebaseio.com/feedbacks.json";

        System.out.println("=========================================");
        System.out.println("   PAINEL DA DIRETORIA: LEITOR DE REVIEWS ");
        System.out.println("=========================================");
        System.out.println("A procurar novos feedbacks no Firebase...\n");

        try {
            // 1. Abre a ligação HTTP GET com o Firebase
            URL url = new URL(urlFirebase);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setRequestProperty("Accept", "application/json");

            int codigoResposta = conexao.getResponseCode();
            if (codigoResposta != 200) {
                System.out.println("⚠️ Erro ao aceder ao banco de dados. Código HTTP: " + codigoResposta);
                return;
            }

            // 2. Lê a resposta JSON vinda do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream(), "UTF-8"));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = in.readLine()) != null) {
                resposta.append(linha);
            }
            in.close();
            conexao.disconnect();

            String json = resposta.toString();

            // Verifica se o nó de feedbacks está vazio
            if (json.equals("null") || json.trim().isEmpty()) {
                System.out.println("Nenhum feedback ou sugestão foi enviado pelos alunos até ao momento.");
                return;
            }

            // 3. Prepara o ficheiro de texto para exportar o Relatório Oficial
            FileWriter arquivoReport = new FileWriter("Relatorio_Feedbacks.txt");
            PrintWriter gravarArquivo = new PrintWriter(arquivoReport);

            gravarArquivo.println("==================================================");
            gravarArquivo.println("       RELATÓRIO IMPRESSO DE FEEDBACKS DOS ALUNOS  ");
            gravarArquivo.println("==================================================");
            gravarArquivo.println("Gerado em tempo real via Sistema Java Backend\n");

            // 4. Expressão Regular (Regex) para varrer o JSON nativamente.
            // O Firebase organiza as chaves por ordem alfabética: "aluno", "data", "mensagem".
            Pattern pattern = Pattern.compile("\"aluno\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"data\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"mensagem\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(json);

            int contador = 0;
            while (matcher.find()) {
                contador++;
                String aluno = matcher.group(1);
                String data = matcher.group(2);
                String mensagem = matcher.group(3);

                // Mostra o resultado formatado no Terminal do Java
                System.out.println("-----------------------------------------");
                System.out.println("Feedback #" + contador);
                System.out.println("📅 Data: " + data);
                System.out.println("👤 Aluno: " + aluno);
                System.out.println("💬 Mensagem: " + mensagem);

                // Escreve os mesmos dados dentro do ficheiro Relatorio_Feedbacks.txt
                gravarArquivo.println("--------------------------------------------------");
                gravarArquivo.println("Feedback Número: " + contador);
                gravarArquivo.println("Data de Envio:   " + data);
                gravarArquivo.println("Nome do Aluno:   " + aluno);
                gravarArquivo.println("Depoimento:      " + mensagem);
            }

            // Finaliza o ficheiro de texto
            gravarArquivo.println("--------------------------------------------------");
            gravarArquivo.println("\nFim do documento. Total de avaliações processadas: " + contador);
            gravarArquivo.close();

            System.out.println("-----------------------------------------");
            System.out.println("\n✅ Processo concluído com sucesso!");
            System.out.println("📥 " + contador + " feedbacks descarregados do Realtime Database.");
            System.out.println("📂 Ficheiro de auditoria criado: 'Relatorio_Feedbacks.txt'");
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao processar o Java: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
