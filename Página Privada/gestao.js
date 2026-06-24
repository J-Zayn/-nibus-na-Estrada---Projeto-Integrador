document.addEventListener('DOMContentLoaded', () => {
    // 1. CONFIGURAÇÃO DO FIREBASE
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

    if (!firebase.apps.length) {
        firebase.initializeApp(firebaseConfig);
    }
    const database = firebase.database();

    const selectMotorista = document.getElementById('sec-motorista');
    const form = document.getElementById('form-secretaria');

    // 2. BUSCA AUTOMATICAMENTE OS MOTORISTAS CADASTRADOS NO BANCO
    database.ref('usuarios/motoristas').once('value').then((snapshot) => {
        selectMotorista.innerHTML = '<option value="">-- Selecione o Motorista --</option>';
        if (snapshot.exists()) {
            snapshot.forEach((child) => {
                const mot = child.val();
                // Usa o nome do motorista como valor da opção
                selectMotorista.innerHTML += `<option value="${mot.nome}">${mot.nome}</option>`;
            });
        } else {
            selectMotorista.innerHTML = '<option value="">Nenhum motorista cadastrado ainda</option>';
        }
    }).catch((error) => {
        selectMotorista.innerHTML = '<option value="">Erro ao carregar motoristas</option>';
        console.error(error);
    });

    // 3. ENVIO DOS DADOS PARA A ESTRUTURA DINÂMICA
    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const cidade = document.getElementById('sec-cidade').value.trim();
        const rota = document.getElementById('sec-rota').value.trim();
        const itinerario = document.getElementById('sec-itinerario').value.trim();
        const inicio = document.getElementById('sec-inicio').value;
        const chegada = document.getElementById('sec-chegada').value;
        const motorista = selectMotorista.value;

        if (!motorista) {
            Swal.fire({
                title: 'Atenção!',
                text: 'Por favor, selecione um motorista válido.',
                icon: 'warning',
                background: '#06241e', color: 'white', confirmButtonColor: '#ffcc00'
            });
            return;
        }

        // Guarda os dados no nó unificado cidades_rotas para o dashboard ler em tempo real
        database.ref(`cidades_rotas/${cidade}/rotas/${rota}`).set({
            itinerario: itinerario,
            inicio: inicio,
            chegada: llegada = chegada,
            motorista: motorista,
            status: "Fora de Operação",
            motivo: "Viagem não iniciada"
        }).then(() => {
            Swal.fire({
                title: 'Rota Criada!',
                text: `A rota "${rota}" em ${cidade} foi configurada com sucesso.`,
                icon: 'success',
                background: '#06241e', color: 'white', confirmButtonColor: '#ffcc00'
            });
            form.reset();
        }).catch((error) => {
            Swal.fire({
                title: 'Erro!',
                text: 'Não foi possível salvar a rota: ' + error.message,
                icon: 'error',
                background: '#06241e', color: 'white', confirmButtonColor: '#ffcc00'
            });
        });
    });
});
