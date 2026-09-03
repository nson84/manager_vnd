import type { CashStatsResponse } from '../types/cashbook.types'
import { formatVnd } from '../types/cashbook.types'

interface CashStatsProps {
  stats: CashStatsResponse | null
  isLoading: boolean
}

export function CashStats({ stats, isLoading }: CashStatsProps) {
  if (isLoading && !stats) {
    return <p className="cash-muted">Đang tải thống kê...</p>
  }

  return (
    <div className="cash-stats">
      <div className="cash-stat in">
        <span>Tổng thu</span>
        <strong>{formatVnd(stats?.totalIn ?? 0)}</strong>
      </div>
      <div className="cash-stat out">
        <span>Tổng chi</span>
        <strong>{formatVnd(stats?.totalOut ?? 0)}</strong>
      </div>
      <div className="cash-stat">
        <span>Tồn quỹ kỳ</span>
        <strong>{formatVnd(stats?.balance ?? 0)}</strong>
      </div>
    </div>
  )
}
