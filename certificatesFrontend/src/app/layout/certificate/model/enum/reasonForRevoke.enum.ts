export enum ReasonForRevoke {
  UNSPECIFIED = 'Unspecified reason for revocation',
  KEY_COMPROMISE = 'Private key compromise',
  CA_COMPROMISE = 'CA compromise',
  AFFILIATION_CHANGED = 'Change in affiliation',
  SUPERSEDED = 'Superseded certificate',
  CESSATION_OF_OPERATION = 'Cessation of operation',
  CERTIFICATE_HOLD = 'Certificate hold - temporary suspension',
  REMOVE_FROM_CRL = 'Removal from Certificate Revocation List',
  PRIVILEGE_WITHDRAWN = 'Withdrawal of associated privileges',
  AACOMPROMISE = 'Attribute authority compromise'
}
