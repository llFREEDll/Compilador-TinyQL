.model small
.stack
.data
lineBreak db 10,13,"$"
dospuntos db " : $"
deletedb db "DB droped$"
dbname db "DB:Persons$"
create0 db "PersonID$"
create1 db "LastName$"
create2 db "Address$"
insert0 db "value1$"
insert1 db "value2$"
insert2 db "value3$"
select0 db "PersonID$"
select1 db "LastName$"
select2 db "Address$"
select3 db "*$"
update0 db "AlfredSchmidt$"
update1 db "Frankfurt$"
update2 db "MX$"
delete0 db "AlfredSchmidt$"
drop0 db "0$"
.code
MAIN PROC FAR  
MOV AX,@DATA
MOV DS,AX
 LEA DX,dbname
MOV AH,09H 
INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create0  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create1  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create2  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create0  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,insert0  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create1  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,insert1  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create2  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,insert2  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create0  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,insert0  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create1  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,insert1  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create2  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,insert2  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create0  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,update0  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create1  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,update1  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create2  
MOV AH,09H 
 INT 21H  
LEA DX,dospuntos  
MOV AH,09H 
 INT 21H  
LEA DX,update2  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create0  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create1  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,create2  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
LEA DX,deletedb  
MOV AH,09H 
 INT 21H  
LEA DX, lineBreak
MOV AH,09H 
INT 21H  
MOV AH,4CH 
 INT 21H  
MAIN ENDP  
END MAIN 