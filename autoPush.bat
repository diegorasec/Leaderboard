@echo off
REM Script para automatizar o processo de git stage, commit e push

REM Navegar até o diretório do repositório (substitua pelo caminho correto)
cd C:\caminho\para\seu\repositorio

REM Adiciona todos os arquivos modificados para o stage
git add .

REM Executa o commit com a mensagem fornecida
git commit -m "Commit Proj"

REM Faz o push para o repositório remoto
git push origin main

pause