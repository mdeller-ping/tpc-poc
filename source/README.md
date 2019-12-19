# Bcrypt and SHA-1 Password Credential Validator

Validates credentials against a JDBC Data Store using Bcrypt and SHA-1.

# Deployment

Place in <PingFederate>/server/default/deploy of appropriate Server Profile (if using Docker) or directly on disk (if running PingFederate locally).  Start PingFederate.

# Configuration

PingFederate Admin Console - System - Password Credential Validators - New Instance

Requires a JDBC Data Store, Database Query and Password Hash Column Name.
  
# WARNING

THE SOFTWARE PROVIDED HEREUNDER IS PROVIDED ON AN "AS IS" BASIS, WITHOUT ANY WARRANTIES OR REPRESENTATIONS EXPRESS, IMPLIED OR STATUTORY.

NOT PRODUCTION READY.  This Password Credential Validator was built to illustrate specific scenarios in a Proof of Concept.  It is NOT to be used for a production deployment.

