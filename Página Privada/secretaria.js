document.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // 1. CONFIGURAÇÃO DO FIREBASE (Suas Credenciais)
    // ==========================================
    const firebaseConfig = {
        apiKey: "AIzaSyC5ZzXiOe8-Zad44M-AMRc8Yczb5nVurpU",
        authDomain: "onibusnaestrada-ad313.firebaseapp.com",
        databaseURL: "https://onibusnaestrada-ad313-default-rtdb.firebaseio.com",
        projectId: "onibusnaestrada-ad313",
        storageBucket: "onibusnaestrada-ad313.firebasestorage.app",
        messagingSenderId: "432204344739",
        appId: "1:432204344739:web:8b89dd476d59578fabbe16",
        measurementId: "G-LZFC78XWFW"
    };

    firebase.initializeApp(firebaseConfig);
    const database = firebase.database();

    const formAdmin = document.getElementById('form-admin');

    const alertTheme = {
        background: '#06241e',      
        color: '#ffffff',           
        confirmButtonColor: '#ffcc00', 
    };

    // ==========================================
    // 2. FUNÇÃO PARA GERAR CÓDIGO ALEATÓRIO (6 Dígitos)
    // ==========================================
    function gerarCodigoAleatorio() {
        const caracteres = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        let codigo = '';
        for (let i = 0; i < 6; i++) {
            codigo += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
        }
        return codigo;
    }

    // ==========================================
    // 3. ENVIO DO FORMULÁRIO E SALVAMENTO NO BANCO
    // ==========================================
    formAdmin.addEventListener('submit', (e) => {
        e.preventDefault();

        const nomeMotorista = document.getElementById('driver-name-admin').value.trim();
        const codigoGerado = gerarCodigoAleatorio();

        // Salva na pasta 'codigos_autorizados' usando o próprio código como chave principal
        database.ref('codigos_autorizados/' + codigoGerado).set({
            nome: nomeMotorista,
            usado: false // Começa como falso porque acabou de ser gerado
        })
        .then(() => {
            // Limpa o campo do formulário
            document.getElementById('driver-name-admin').value = '';

            // Exibe o alerta com o código gigante para a secretária copiar
            Swal.fire({
                ...alertTheme,
                title: 'Código Criado!',
                html: `O código para o motorista <b>${nomeMotorista}</b> é:<br><br><span class="text-3xl font-extrabold text-[#ffcc00] tracking-widest bg-[#031411] px-4 py-2 rounded-xl block w-fit mx-auto border border-[#0c4035] select-all">${codigoGerado}</span><br>Copie e envie para ele via WhatsApp.`,
                icon: 'success',
                confirmButtonText: 'Pronto'
            });
        })
        .catch((error) => {
            Swal.fire({ ...alertTheme, title: 'Erro ao gerar', text: error.message, icon: 'error' });
        });
    });
});
