create table `link_v2`
(
    id            bigint auto_increment primary key,
    guid          binary(16)       not null,
    url           varchar(2048)    not null,
    is_accessible bit default b'1' not null,
    created_at    datetime(6)      not null,
    updated_at    datetime(6)      not null,
    constraint UKhsobr1elx8v8phbqymq08sifd
        unique (guid)
);

create table article_information
(
    id         bigint auto_increment primary key,
    image_url  text        null,
    title      text        null,
    summary    text        null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null
);

create table article
(
    id             bigint auto_increment primary key,
    link_id        bigint      null,
    information_id bigint      null,
    created_at     datetime(6) not null,
    updated_at     datetime(6) not null,
    constraint UKa1o1pkpy09ngnhyur344dq8kp
        unique (information_id),
    constraint UKhcr91j53wrjmec2hwphfpgokx
        unique (link_id),
    constraint FK8gsrxlo8iat28wlr8d3q5f0ct
        foreign key (link_id) references `link_v2` (id),
    constraint FKm3qh63x5ta2gvgf6kafdl3084
        foreign key (information_id) references article_information (id)
);

create table category
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    name       varchar(255) not null,
    constraint UK46ccwnsi9409t36lurvtyljak
        unique (name)
);

create table keyword
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    name       varchar(255) not null,
    constraint UKhvq9bm3mbguqoicyv02g5crjs
        unique (name)
);

create table article_category
(
    catetory_id bigint null,
    article_id  bigint not null
        primary key,
    constraint FKd61g098hytbe0i2irhjmffk4n
        foreign key (catetory_id) references category (id),
    constraint FKrw5912jiy0vyqoyqlo5r65igk
        foreign key (article_id) references article (id)
);

create table article_keyword
(
    article_id bigint not null,
    keyword_id bigint not null,
    primary key (article_id, keyword_id),
    constraint FK71143jtrpbywwr8ys2eppe7c2
        foreign key (article_id) references article (id),
    constraint FKkuyvit2pwelitlj3aulwqrsb5
        foreign key (keyword_id) references keyword (id)
);