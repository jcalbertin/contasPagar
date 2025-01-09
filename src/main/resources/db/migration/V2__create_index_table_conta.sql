CREATE INDEX IF NOT EXISTS idx_conta_data_pagamento_situacao_id
    ON conta (data_pagamento, situacao, id);

CREATE INDEX IF NOT EXISTS idx_conta_data_vencimento_situacao_descricao_id
    ON conta (data_vencimento, situacao, descricao, id);