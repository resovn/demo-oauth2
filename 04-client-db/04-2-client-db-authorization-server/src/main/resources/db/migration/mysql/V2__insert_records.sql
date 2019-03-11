INSERT INTO oauth_client_details
(client_id, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES ('client1', '{noop}123456', 'read,write', 'client_credentials',
        NULL, 'USER', NULL,
        NULL, NULL, FALSE);

INSERT INTO oauth_client_details
(client_id, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES ('client2', '{noop}123456', 'read,write', 'password,refresh_token',
        NULL, 'USER', 36000,
        36000, NULL, FALSE);
