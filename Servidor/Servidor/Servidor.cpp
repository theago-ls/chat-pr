#include <winsock2.h>
#include <ws2tcpip.h>
#include <iostream>
#include <Windows.h>
#include <process.h>
#include <time.h>
#include <unordered_map>
#include <string>

#pragma comment (lib, "Ws2_32.lib")

#define PORT "45000"
#define VER "1"
#define FILL_CHAR ' '
#define VERSION 1
#define TIPO 3
#define FROM 20
#define TO 20
#define TAMANHO 5
#define DADOS 20000
#define ALL_LEN 20049

void criaConexao(void *arg);
void iniciaConexao(void *arg);
void liv();
void update();

using namespace std;

unordered_map<string, SOCKET> clientes;
unordered_map<string, int> contadorLiv;
unordered_map<string, pair<HANDLE,HANDLE>> clientesMutexes;


bool livFlag = true;
HANDLE clienteMutex;

typedef struct parametros {
	SOCKET ListenSocket;
	SOCKET ClienteSocket;
}PARAM;

int main() {
	WSADATA wsaData;
	int iResult;
	PARAM par;
	HANDLE Thread;
	DWORD TName;

	clienteMutex = CreateMutex(NULL, FALSE, NULL);		

	//inicializando o Winsock indicando a versão do Winsock
	iResult = WSAStartup(MAKEWORD(2, 2), &wsaData);//MAKEWORD(2,2) indica qual versão do Winsock está sendo utilizada
	if (iResult != 0) {
		printf("WSAStartup falhou. Erro %d\n", iResult);
		return 1;
	}

	//Criando o Winsock no servidor

	struct addrinfo hints;
	struct addrinfo *result = NULL;

	ZeroMemory(&hints, sizeof(hints));
	hints.ai_family = AF_INET; //especifica que o endereçamento IP é IPV4
	hints.ai_socktype = SOCK_STREAM; //especifica o socket stream
	hints.ai_protocol = IPPROTO_TCP; //especifica o protocolo da camada de transporte
	hints.ai_flags = AI_PASSIVE;

	//Define o endereço e a porta do servidor
	iResult = getaddrinfo(NULL, PORT, &hints, &result);
	if (iResult != 0) {
		printf("Gettaddrinfo falhou. Erro %d\n", iResult);
		WSACleanup();
		return 1;
	}


	//Cria o socket para ser usado no servidor
	SOCKET ListenSocket = INVALID_SOCKET;
	SOCKET ClienteSocket = INVALID_SOCKET;

	ListenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);
	if (ListenSocket == INVALID_SOCKET) {
		printf("Falha na criacao do socket. Erro: %ld\n", WSAGetLastError());
		freeaddrinfo(result);
		WSACleanup();
		return 1;
	}

	iResult = bind(ListenSocket, result->ai_addr, (int)result->ai_addrlen);
	if (iResult == SOCKET_ERROR) {
		printf("Bind falhou. Erro: %d\n", WSAGetLastError());
		freeaddrinfo(result);
		closesocket(ListenSocket);
		WSACleanup();
		return 1;
	}

	par.ListenSocket = ListenSocket;

	freeaddrinfo(result);

	//Chama a funcao para ouvir passando como parametro o comprimento máximo da fila.
	Thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)criaConexao, &par, 0, &TName);
	
	WaitForSingleObject(Thread,INFINITE);
	livFlag = false;
	return 0;
}

void criaConexao(void *arg) {
	PARAM* par = (PARAM*)arg;

	int iResult, i = 0;
	HANDLE Thread, ThreadLIV;	
	DWORD TName, TNameLIV;

	ThreadLIV = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)liv, NULL, 0, &TNameLIV);	
	printf("Aguardando conexoes...\n\n");
	while (true) {
		iResult = listen(par->ListenSocket, SOMAXCONN);
		if (iResult == SOCKET_ERROR) {
			printf("Listen falhou. Erro: %d\n", WSAGetLastError());
			closesocket(par->ListenSocket);
			WSACleanup();
			system("PAUSE");
			break;
		}

		par->ClienteSocket = accept(par->ListenSocket, NULL, NULL);
		printf("\nConexao iniciada.\n");
		if (par->ClienteSocket == INVALID_SOCKET) {
			printf("Accept falhou. Erro %d\n", WSAGetLastError());
			closesocket(par->ListenSocket);
			WSACleanup();
			system("PAUSE");
			break;
		}		
		Thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)iniciaConexao, &par->ClienteSocket, 0, &TName);		
	}
}

void iniciaConexao(void *arg) {
	SOCKET *sock = (SOCKET*)arg;
	SOCKET ClientSocket = *sock;
	bool live = true;

	string sendBuffer(ALL_LEN, FILL_CHAR);
	string version(VERSION, FILL_CHAR);
	string type(TIPO, FILL_CHAR);
	string from(FROM, FILL_CHAR);
	string to(TO, FILL_CHAR);
	string tam(TAMANHO, FILL_CHAR);
	string dados(DADOS, FILL_CHAR);
	string recvBuffer;
	string recvVersion;
	string recvType;
	string recvFrom;
	string recvTo;
	string recvTam;
	string recvData;
	char recvBuff[ALL_LEN] = {};
	char sendBuff[ALL_LEN] = {};
	int iResult;

	
	iResult = recv(ClientSocket, recvBuff, ALL_LEN, 0);
	if (iResult <= 0) {
		printf("Receive falhou. Erro: %d\n", WSAGetLastError());
		closesocket(ClientSocket);
		printf("\n\nConexao finalizada.\n");
		_endthread();
	}
	recvBuffer = recvBuff;
	recvVersion = recvBuffer.substr(0, VERSION);
	recvType = recvBuffer.substr(VERSION, TIPO);
	recvFrom = recvBuffer.substr(VERSION + TIPO, FROM);


	printf("\nServidor - Versao recebida: %s", recvVersion.c_str());
	printf("\nServidor - Tipo recebido: %s", recvType.c_str());
	printf("\nServidor - Nickname recebido: %s\n", recvFrom.c_str());

	while (clientes.count(recvFrom)) {
		version.replace(0, VERSION, VER);
		type.replace(0, TIPO, "ACK");
		tam = "5    ";

		dados.replace(0, 4, "FALSE");
		sendBuffer.replace(0, VERSION, version);
		sendBuffer.replace(VERSION, VERSION + TIPO, type);
		sendBuffer.replace(VERSION + TIPO, VERSION + TIPO + FROM, from);
		sendBuffer.replace(VERSION + TIPO + FROM, VERSION + TIPO + FROM + TO, to);
		sendBuffer.replace(VERSION + TIPO + FROM + TO, VERSION + TIPO + FROM + TO + TAMANHO, tam);
		sendBuffer.replace(VERSION + TIPO + FROM + TO + TAMANHO, VERSION + TIPO + FROM + TO + TAMANHO + DADOS, dados);

		iResult = send(ClientSocket, sendBuffer.c_str(), ALL_LEN, 0);
		if (iResult == SOCKET_ERROR) {
			printf("Send falhou: %d\n", WSAGetLastError());
			closesocket(ClientSocket);
			printf("Conexao finalizada.\n");
			_endthread();
		}

		iResult = recv(ClientSocket, recvBuff, ALL_LEN, 0);
		if (iResult <= 0) {
			printf("Receive falhou. Erro: %d\n", WSAGetLastError());
			closesocket(ClientSocket);
			printf("Conexao finalizada.\n");
			_endthread();
		}

		recvBuffer = recvBuff;
		recvVersion = recvBuffer.substr(0, VERSION);
		recvType = recvBuffer.substr(VERSION, TIPO);
		recvFrom = recvBuffer.substr(VERSION + TIPO, FROM);

		printf("\nServidor - Versao recebida: %s", recvVersion.c_str());
		printf("\nServidor - Tipo recebido: %s", recvType.c_str());
		printf("\nServidor - Nickname recebido: %s", recvFrom.c_str());
	}

	version.replace(0, VERSION, VER);
	type.replace(0, TIPO, "ACK");

	tam = "4    ";

	dados.replace(0, 3, "TRUE");
	sendBuffer.replace(0, VERSION, version);
	sendBuffer.replace(VERSION, VERSION + TIPO, type);
	sendBuffer.replace(VERSION + TIPO, VERSION + TIPO + FROM, from);
	sendBuffer.replace(VERSION + TIPO + FROM, VERSION + TIPO + FROM + TO, to);
	sendBuffer.replace(VERSION + TIPO + FROM + TO, VERSION + TIPO + FROM + TO + TAMANHO, tam);
	sendBuffer.replace(VERSION + TIPO + FROM + TO + TAMANHO, VERSION + TIPO + FROM + TO + TAMANHO + DADOS, dados);
	
	string nickname = recvFrom;

	HANDLE saidaMutex = CreateMutex(NULL, FALSE, NULL);
	HANDLE entradaMutex = CreateMutex(NULL, FALSE, NULL);
	

	clientesMutexes[recvFrom] = pair<HANDLE, HANDLE>(entradaMutex, saidaMutex);
	WaitForSingleObject(saidaMutex, INFINITE);
	iResult = send(ClientSocket, sendBuffer.c_str(), ALL_LEN, 0);
	if (iResult == SOCKET_ERROR) {
		printf("\nSend de ACK de nickname do %s falhou: %d\n",nickname.c_str(), WSAGetLastError());
		closesocket(ClientSocket);
		printf("\n\nConexao finalizada.\n");
		_endthread();
	}
	ReleaseMutex(saidaMutex);

	WaitForSingleObject(clienteMutex, INFINITE);
	clientes.insert(pair<string, SOCKET>(recvFrom, ClientSocket));	
	contadorLiv.insert(pair<string, int>(recvFrom, 0));	
	ReleaseMutex(clienteMutex);
	

	bool flagListen = true;
	update();


	int tent = 5;	
	while (flagListen && (clientes.find(nickname) != clientes.end())){
		recvBuffer.clear();		
		WaitForSingleObject(entradaMutex, INFINITE);
		iResult = recv(ClientSocket, recvBuff, ALL_LEN, 0);			
		if (iResult <= 0) {
			printf("\nReceive de %s falhou. Erro %d\n",nickname.c_str(),WSAGetLastError());				
			tent--;
			ReleaseMutex(entradaMutex);			
			if (tent == 0) {				
				break;
			}
			Sleep(20000);
		}		
		else {	
			ReleaseMutex(entradaMutex);
			recvBuffer = recvBuff;
			recvVersion = recvBuffer.substr(0, VERSION);
			recvType = recvBuffer.substr(VERSION, TIPO);
			recvFrom = recvBuffer.substr(VERSION + TIPO, FROM);			

			if (recvType == "MEN") {
				recvTo = recvBuffer.substr(VERSION + TIPO + FROM, TO);	
				WaitForSingleObject(clienteMutex, INFINITE);
				unordered_map<string, SOCKET>::iterator aux = clientes.find(recvTo);
				if (aux != clientes.end()) {
					SOCKET to = aux->second;
					WaitForSingleObject(saidaMutex, INFINITE);					
					iResult = send(to, recvBuff, sizeof(recvBuff), 0);
					if (iResult == SOCKET_ERROR) {
						printf("\nSend de MEN de %s falhou. Erro %d\n", nickname.c_str(), WSAGetLastError());
					}					
					//printf("\nMEN de %s para %s.\n", nickname.c_str(), aux->first.c_str());		
					ReleaseMutex(saidaMutex);
				}
				ReleaseMutex(clienteMutex);
			}
			
			if (recvType == "ERA") {				
				unordered_map<string, SOCKET>::iterator aux = clientes.find(recvFrom);
				SOCKET to = aux->second;				

				WaitForSingleObject(saidaMutex, INFINITE);
				iResult = send(to, sendBuffer.c_str(), ALL_LEN, 0);
				if (iResult == SOCKET_ERROR) {
					printf("\nSend de ACK respondendo ERA de %s falhou. Erro %d\n", nickname.c_str(), WSAGetLastError());					
				}
				ReleaseMutex(saidaMutex);
				WaitForSingleObject(clienteMutex, INFINITE);
				clientes.erase(aux->first);
				closesocket(ClientSocket);
				printf("\n\nConexao com %s finalizada.\n", nickname.c_str());
				ReleaseMutex(clienteMutex);
				update();
				flagListen = false;
			}			
		}
	}

	CloseHandle(saidaMutex);
	CloseHandle(entradaMutex);

	clientesMutexes.erase(clientesMutexes.find(nickname));
}


void update() {
	string sendBuffer(ALL_LEN, FILL_CHAR);
	string version(VERSION, FILL_CHAR);
	string type(TIPO, FILL_CHAR);
	string from(FROM, FILL_CHAR);
	string to(TO, FILL_CHAR);
	string tam(TAMANHO, FILL_CHAR);
	string dados = "";

	version.replace(0, VERSION, VER);
	type.replace(0, TIPO, "UPD");
	

	WaitForSingleObject(clienteMutex, INFINITE);	
	for (unordered_map<string, SOCKET>::iterator it2 = clientes.begin(); it2 != clientes.end(); it2++) {
		dados += it2->first + ";";
	}
		
	char str[6];
	snprintf(str, 6, "%05d", dados.size());
		
	tam = str;
	
	for (unordered_map<string, SOCKET>::iterator it = clientes.begin(); it != clientes.end(); it++) {
		SOCKET cliente = it->second;
		
		sendBuffer.replace(0, VERSION, version);
		sendBuffer.replace(VERSION, VERSION + TIPO, type);
		sendBuffer.replace(VERSION + TIPO, VERSION + TIPO + FROM, from);
		sendBuffer.replace(VERSION + TIPO + FROM, VERSION + TIPO + FROM + TO, to);
		sendBuffer.replace(VERSION + TIPO + FROM + TO, VERSION + TIPO + FROM + TO + TAMANHO, tam);
		sendBuffer.replace(VERSION + TIPO + FROM + TO + TAMANHO, VERSION + TIPO + FROM + TO + TAMANHO + DADOS, dados);

		unordered_map<string, pair<HANDLE,HANDLE>>::iterator it2 = clientesMutexes.find(it->first);


		HANDLE saidaMutex = it2->second.second;

		WaitForSingleObject(saidaMutex, INFINITE);
		int iResult = send(cliente, sendBuffer.c_str(), ALL_LEN, 0);
		if (iResult == SOCKET_ERROR) {
			printf("\nSend de UPD para %s falhou. Erro %d\n", it->first.c_str(), WSAGetLastError());		
		}		
		ReleaseMutex(saidaMutex);
	}
	ReleaseMutex(clienteMutex);
}

void liv() {
	while (livFlag) {
		Sleep(30000);
		string sendBuffer(ALL_LEN, FILL_CHAR);
		string version(VERSION, FILL_CHAR);
		string type(TIPO, FILL_CHAR);
		string from(FROM, FILL_CHAR);
		string to(TO, FILL_CHAR);
		string tam(TAMANHO, FILL_CHAR);
		string dados(DADOS, FILL_CHAR);
		string recvBuffer;
		string recvVersion;
		string recvType;				
		char recvBuff[ALL_LEN] = {};

		version.replace(0, VERSION, VER);
		type.replace(0, TIPO, "LIV");

		char str[6];
		snprintf(str, 6, "%05d", 0);

		tam = str;

		bool flagUpd = false;

		if (!clientes.empty()) {
			WaitForSingleObject(clienteMutex, INFINITE);
			for (unordered_map<string, SOCKET>::iterator it = clientes.begin(); it != clientes.end(); it++) {
				from = it->first;
				while (from.size() < FROM) 
					from += FILL_CHAR;				

				string nickname = it->first;
				SOCKET cliente = it->second;
				
				sendBuffer.replace(0, VERSION, version);
				sendBuffer.replace(VERSION, VERSION + TIPO, type);
				sendBuffer.replace(VERSION + TIPO, VERSION + TIPO + FROM, from);
				sendBuffer.replace(VERSION + TIPO + FROM, VERSION + TIPO + FROM + TO, to);
				sendBuffer.replace(VERSION + TIPO + FROM + TO, VERSION + TIPO + FROM + TO + TAMANHO, tam);
				sendBuffer.replace(VERSION + TIPO + FROM + TO + TAMANHO, VERSION + TIPO + FROM + TO + TAMANHO + DADOS, dados);

				unordered_map<string, pair<HANDLE, HANDLE>>::iterator it2 = clientesMutexes.find(nickname);

				HANDLE saidaMutex = it2->second.second;
				HANDLE entradaMutex = it2->second.first;

				WaitForSingleObject(saidaMutex, INFINITE);
				int iResult = send(cliente, sendBuffer.c_str(), ALL_LEN, 0);
				if (iResult == SOCKET_ERROR)
					printf("\nSend de LIV para %s falhou: %d\n", nickname.c_str(), WSAGetLastError());				
				ReleaseMutex(saidaMutex);

				WaitForSingleObject(entradaMutex, INFINITE);
				iResult = recv(cliente, recvBuff, ALL_LEN, 0);
				ReleaseMutex(entradaMutex);
				if (iResult <= 0) {					
					printf("\nReceive de LIV para %s falhou. Erro: %d\n", nickname.c_str(), WSAGetLastError());	
					unordered_map<string, int>::iterator it = contadorLiv.find(nickname);
					if (it != contadorLiv.end()) {
						if (it->second == 2) {		
							printf("\nCliente %s desconectado por falta de resposta de LIV.\n", nickname.c_str());
							unordered_map<string, SOCKET>::iterator it2 = clientes.find(it->first);
							unordered_map<string, SOCKET>::iterator it3 = clientes.erase(it2);
							clientesMutexes.erase(clientesMutexes.find(it->first));
							contadorLiv.erase(contadorLiv.find(it->first));
							closesocket(cliente);
							flagUpd = true;
							if (it3 == clientes.end())
								break;
						}
						else {
							it->second++;
						}
					}					
				}				
			}
			ReleaseMutex(clienteMutex);
			if (flagUpd)
				update();			
		}
	}
}
