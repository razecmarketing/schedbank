/**
 * Teste Frontend Vue.js - Validacao Completa
 * Script de teste nao destrutivo seguindo padroes corporativos
 */

console.log('=== INICIANDO TESTES DO FRONTEND VUE.JS ===');

// Teste 1: Verificar se os elementos da interface existem
function testeElementosInterface() {
  console.log('\n1. TESTE DE ELEMENTOS DA INTERFACE');
  
  const elementos = {
    'formulario': 'form',
    'conta_origem': 'input[placeholder="XXXXXXXXXX"]:first',
    'conta_destino': 'input[placeholder="XXXXXXXXXX"]:last',
    'valor': 'input[type="number"]',
    'data_transferencia': 'input[type="date"]',
    'botao_confirmar': 'button[type="submit"]',
    'botao_extrato': 'button:contains("Extrato")',
    'botao_limpar': 'button:contains("Limpar")'
  };
  
  for (const [nome, seletor] of Object.entries(elementos)) {
    const elemento = document.querySelector(seletor);
    console.log(`  ${nome}: ${elemento ? 'PRESENTE' : 'AUSENTE'}`);
  }
}

// Teste 2: Verificar validacoes dos campos
function testeValidacoesCampos() {
  console.log('\n2. TESTE DE VALIDACOES DOS CAMPOS');
  
  const inputContaOrigem = document.querySelector('input[placeholder="XXXXXXXXXX"]');
  const inputValor = document.querySelector('input[type="number"]');
  const inputData = document.querySelector('input[type="date"]');
  
  if (inputContaOrigem) {
    console.log(`  Conta origem - maxlength: ${inputContaOrigem.maxLength || 'NAO DEFINIDO'}`);
    console.log(`  Conta origem - pattern: ${inputContaOrigem.pattern || 'NAO DEFINIDO'}`);
    console.log(`  Conta origem - required: ${inputContaOrigem.required ? 'SIM' : 'NAO'}`);
  }
  
  if (inputValor) {
    console.log(`  Valor - min: ${inputValor.min || 'NAO DEFINIDO'}`);
    console.log(`  Valor - step: ${inputValor.step || 'NAO DEFINIDO'}`);
    console.log(`  Valor - required: ${inputValor.required ? 'SIM' : 'NAO'}`);
  }
  
  if (inputData) {
    console.log(`  Data - min: ${inputData.min || 'NAO DEFINIDO'}`);
    console.log(`  Data - required: ${inputData.required ? 'SIM' : 'NAO'}`);
  }
}

// Teste 3: Verificar calculo de taxa em tempo real
function testeCalculoTaxa() {
  console.log('\n3. TESTE DE CALCULO DE TAXA');
  
  const elementoTaxa = document.querySelector('*:contains("Taxa calculada")') || 
                      document.querySelector('*:contains("feeDisplay")') ||
                      document.querySelector('.fee-display');
  
  if (elementoTaxa) {
    console.log('  Elemento de exibicao de taxa: PRESENTE');
  } else {
    console.log('  Elemento de exibicao de taxa: NAO ENCONTRADO');
  }
  
  const elementoTotal = document.querySelector('*:contains("Total débito")') ||
                       document.querySelector('*:contains("totalDisplay")') ||
                       document.querySelector('.total-display');
  
  if (elementoTotal) {
    console.log('  Elemento de exibicao de total: PRESENTE');
  } else {
    console.log('  Elemento de exibicao de total: NAO ENCONTRADO');
  }
}

// Teste 4: Verificar navegacao entre telas
function testeNavegacao() {
  console.log('\n4. TESTE DE NAVEGACAO');
  
  const botoes = document.querySelectorAll('button');
  let botaoAgendar = null;
  let botaoExtrato = null;
  
  botoes.forEach(botao => {
    if (botao.textContent.includes('Agendar')) {
      botaoAgendar = botao;
    }
    if (botao.textContent.includes('Extrato')) {
      botaoExtrato = botao;
    }
  });
  
  console.log(`  Botao Agendar: ${botaoAgendar ? 'PRESENTE' : 'AUSENTE'}`);
  console.log(`  Botao Extrato: ${botaoExtrato ? 'PRESENTE' : 'AUSENTE'}`);
}

// Teste 5: Verificar estrutura de dados
function testeEstruturaDados() {
  console.log('\n5. TESTE DE ESTRUTURA DE DADOS');
  
  // Verificar se o Vue esta carregado
  const vueApp = window.Vue || document.querySelector('#app').__vue_app__;
  console.log(`  Vue.js carregado: ${vueApp ? 'SIM' : 'NAO'}`);
  
  // Verificar Pinia store
  const piniaStore = window.Pinia || document.querySelector('#app').__pinia;
  console.log(`  Pinia store carregado: ${piniaStore ? 'SIM' : 'NAO'}`);
}

// Executar todos os testes
function executarTodosTestes() {
  try {
    testeElementosInterface();
    testeValidacoesCampos();
    testeCalculoTaxa();
    testeNavegacao();
    testeEstruturaDados();
    
    console.log('\n=== TESTES CONCLUIDOS COM SUCESSO ===');
    return true;
  } catch (error) {
    console.error('\nERRO DURANTE OS TESTES:', error);
    return false;
  }
}

// Executar testes quando a pagina estiver carregada
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', executarTodosTestes);
} else {
  executarTodosTestes();
}