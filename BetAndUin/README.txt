|-------------------------------------------------------------------------------------------------|
|>>>>>>>>>>>>>>>>>>>>>>>>INSTRUÇÕES DE INICIAÇÃO DAS APLICAÇÕES<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|
|-------------------------------------------------------------------------------------------------|
Para iniciar um servidor:

	java -jar [fileName] [serverNumber] [partnerIpAddress] -debugging
	
		[serverNumber]		    Corresponde ao número do servidor, de modo a serem
								carregadas as definições de portos correctas.
								O primeiro servidor é o servidor primário por defeito
								enquanto o segundo é considerado secundário.
								Para este campo, digitar 1 ou 2 conforme se pretende
								activar o primeiro ou segundo servidor.
								Não são permitidas repetições dos números, isto é,
								não se pode iniciar simulataneamente mais do que um
								servidor com o mesmo número.
						
		[partnerIpAddress]  	Corresponde ao endereço IP do outro servidor com o qual
								este servidor vai interagir.
						
		-debugging				'Flag' opcional que obriga o servidor a ir imprimindo
								mensagens de 'debugging' ao longo da execução, como o
								tipo de mensagens que envia, recebe, a que portos se está
								a ligar e quando ocorre 'timeouts', por exemplo.
						
						
Para iniciar um cliente, tanto como TCP como RMI:

	java -jar [fileName] [firstServerIpAddress] [secondServerIpAddress]
	
		[firstServerIpAddress]  Corresponde ao endereço IP ao qual este cliente irá tentar
								ligar-se em primeiro lugar. De uma forma geral, aconselha-se
								que este endereço seja igual ao do servidor primário por
								defeito (servidor 1).
						
		[secondServerIpAddress] Corresponde ao endereço IP do segundo servidor, sendo recomendado
								que corresponda ao endereço do servidor 2.
								
								
|-------------------------------------------------------------------------------------------------|								
|>>>>>>>>>>>>>>>>>>>>>>>>INSTRUÇÕES CONFIGURAÇÃO DAS APLICAÇÕES<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|
|-------------------------------------------------------------------------------------------------|

No ficheiro de configuração 'properties.conf', encontram-se os seguintes campos:
	
	-> NO_RETRIES
		Número de tentativas para se ligar a um servidor que um cliente efectua antes te tentar
		o segundo servidor ou então desistir.
	
	-> CLIENT_WAITING_TIME
		Tempo de espera entre duas tentativas consecutivas de se ligar a um servidor.
			
	-> DEFAULT_CREDITS
		Número de créditos que são dados por defeito a um cliente. Este número também é usado
		quando um cliente efectua um 'reset'.

	-> BUFFER_SIZE
		Tamanho do 'buffer' que guardas as mensagens enquanto a ligação está em baixo.
	
	-> TIME_BETWEEN_ROUNDS
		Tempo de duração de uma ronda de jogos.
			
	-> NO_GAMES
		Número de jogos por ronda.
						
	-> SERVER_INIT_RETRIES
		Número máximo de mensagens 'I_WILL_BE_PRIMARY_SERVER' que um servidor envia ao iniciar,
		antes de considerar o outro servidor desligado.
	
	-> FIRST_TCP_SERVER_PORT
		Porto do primeiro servidor ao qual os clientes TCP se ligam.
	
	-> SECOND_TCP_SERVER_PORT
		Porto do segundo servidor ao qual os clientes TCP se ligam.
	
	-> FIRST_RMI_SERVER_PORT
		Porto em que o primeiro servidor regista os seus objectos remotos.
			
	-> SECOND_RMI_SERVER_PORT
		Porto em que o segundo servidor regista os seus objectos remotos.
			
	-> STONITH_FIRST_SERVER_PORT
		Porto ao qual o segundo servidor se liga de modo a simular a ligação STONITH.
				
	-> STONITH_SECOND_SERVER_PORT
		Porto ao qual o primeiro servidor se liga de modo a simular a ligação STONITH.
	
	-> SERVER_WAITING_TIME
		Tempo para que o servidor considere o seu companheiro desactivado. Se ao fim deste
		período o servidor não receber nenhuma mensagem vinda do outro servidor, ocorre um
		'timeout' e o servidor inicia os procedimentos adequados.
		
	-> FIRST_WAITING_TIME
		Tempo de espera entre o envio de duas mensagens 'I_WILL_BE_PRIMARY_SERVER' no início
		do estabelecimento da ligação, caso não tenha obtido resposta à primeira.
		
	-> KEEP_ALIVE_TIME
		Tempo entre o envio de duas mensagens 'KEEP_ALIVE'. Naturalmente, tem de ser maior
		do que SERVER_WAITING_TIME.
	

Por defeito, são feitas as seguintes configurações:

NO_GAMES=10
BUFFER_SIZE=10
SECOND_TCP_SERVER_PORT=7000
STONITH_FIRST_SERVER_PORT=8000
SERVER_WAITING_TIME=15000
FIRST_WAITING_TIME=5000
KEEP_ALIVE_TIME=5000
SERVER_INIT_RETRIES=3
NO_RETRIES=10
DEFAULT_CREDITS=100
CLIENT_WAITING_TIME=1000
FIRST_TCP_SERVER_PORT=6000
SECOND_RMI_SERVER_PORT=13000
TIME_BETWEEN_ROUNDS=60000
STONITH_SECOND_SERVER_PORT=9000
FIRST_RMI_SERVER_PORT=12000