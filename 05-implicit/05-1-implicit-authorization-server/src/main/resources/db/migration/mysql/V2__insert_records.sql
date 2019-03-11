INSERT INTO oauth_client_details
(client_id, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES ('client1', '{noop}123456', 'read,write', 'implicit',
        'http://localhost:8081/callback', 'USER', NULL,
        NULL, NULL, FALSE);
