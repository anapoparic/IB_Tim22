export enum ReasonForRevoke {
    UNSPECIFIED = 'Neodređeni razlog za poništenje certifikata.',
    KEY_COMPROMISE = 'Kompromitiranje privatnog ključa.',
    CA_COMPROMISE = 'Kompromitiranje certifikacijskog autoriteta (CA).',
    AFFILIATION_CHANGED = 'Promjena povezanosti s organizacijom ili entitetom.',
    SUPERSEDED = 'Certifikat je zamijenjen novijim certifikatom.',
    CESSATION_OF_OPERATION = 'Prekid operacija povezanih s certifikatom.',
    CERTIFICATE_HOLD = 'Držanje certifikata - privremeno suspendiranje.',
    REMOVE_FROM_CRL = 'Uklanjanje iz popisa opozvanih certifikata (CRL).',
    PRIVILEGE_WITHDRAWN = 'Povlačenje privilegija povezanih s certifikatom.',
    AACOMPROMISE = 'Kompromitiranje atributa povjerenja.'
}
