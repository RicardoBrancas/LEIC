create table categoria(
  nome varchar(80) not null,
  primary key(nome),
  check(nome in (select nome   --RE1, RE2
                from (select nome from categoria_simples UNION
                      select nome from super_categoria)
                having count(nome) = 1))
);

create table categoria_simples(
  nome varchar(80) not null,
  primary key(nome),
  foreign key(nome)
    references categoria(nome)
);

create table super_categoria(
  nome varchar(80) not null,
  primary key(nome),
  foreign key(nome) references categoria(nome),
  check (nome in (select super_categoria from constituida)) --sor ricardo, has de ver isto
);

create table constituida(
  super_categoria varchar(80) not null,
  categoria varchar(80) not null,
  primary key(super_categoria, categoria),
  foreign key(super_categoria) references super_categoria(nome),
  foreign key(categoria) references categoria(nome),
  check(super_categoria <> categoria)
);

create table produto(
  ean numeric(13) not null,
  design varchar(80) not null,
  categoria varchar(80) not null,
  forn_primario varchar(80) not null,
  primary key(ean),
  foreign key(categoria) references categoria(nome),
  foreign key(forn_primario) references fornecedor(nif)
);

create table fornecedor(
  nif numeric(9) not null,
  nome varchar(80) not null,
  primary key(nif)
);

create table fornece_sec(
  nif numeric(9) not null,
  ean numeric(13) not null,
  foreign key(nif) references fornecedor(nif),
  foreign key(ean) references produto(ean)
);

create table corredor(
  nro varchar(4) not null,
  largura varchar(3) not null,
  primary key(nro)
);

create table prateleira(
  nro varchar(4) not null,
  lado varchar(10) not null,
  altura varchar(10) not null,
  primary key(nro, lado, altura),
  foreign key(nro) references corredor(nro),
  check (lado in ('esquerdo', 'direito')),
  check (altura in ('chao', 'medio', 'superior'))
);

create table planograma(
  ean numeric(13) not null,
  nro varchar(4) not null,
  lado varchar(10) not null,
  altura varchar(10) not null,
  face varchar(5) not null,
  unidades varchar(4) not null,
  primary key(ean, nro, lado, altura),
  foreign key(ean) references produto(ean),
  foreign key(nro, lado, altura) references prateleira(nro, lado, altura)
);

create table evento_reposição(
  operador varchar(80) not null,
  instante timestamp not null,
  primary key(operador),
  primary key(instante),
  check(instante > CURRENT_TIMESTAMP)
);

create table reposicao(
  ean numeric(13) not null,
  nro varchar(4) not null,
  lado varchar(10) not null,
  altura varchar(10) not null,
  unidades varchar(80) not null,
  primary key(ean, nro, lado, altura),
  primary key(operador),
  primary key(instante),
  foreign key(ean, nro, lado, altura) references planograma(ean, nro, lado, altura),
);
