import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AnalyticsService } from '../analytics.service';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-single-accommodation-analytics',
  templateUrl: './single-accommodation-analytics.component.html',
  styleUrls: ['./single-accommodation-analytics.component.css']
})
export class SingleAccommodationAnalyticsComponent implements OnInit {
  @Input() startDate: string = '';
  @Input() endDate: string = '';
  @Input() accommodationId: number = 1;

  @ViewChild(BaseChartDirective, { static: true }) chart!: BaseChartDirective;
  @ViewChild('leftChart', { static: false }) leftChart!: BaseChartDirective;


  chartType: ChartType;
  chartEarningsData: any[] = [];
  chartReservationsData: any[] = [];
  chartLabels: string[] = [];
  chartData: any[] = [];
  chartOptions: any = {
    responsive: true,
  };
  chartLegend = true;

  constructor(
    private analyticsService: AnalyticsService,
    private authService: AuthService
  ) {
    this.chartType = 'bar';
  }

  ngOnInit(): void {
    this.getAnalytics();
  }

  getAnalytics(): void {
    this.analyticsService.getSingleAnalytics(this.startDate, this.endDate, this.authService.getUserID(), this.accommodationId).subscribe({
      next: (analytics) => {
        console.log(analytics);
  
        const chartData: any[] = [];
  
        analytics.forEach((analytic, index) => {
          chartData.push({
            month: analytic.month,
            profit: analytic.totalEarnings!,
            reservations: analytic.totalReservations!
          });
        });
  
        const uniqueMonths = Array.from(new Set(chartData.map(data => data.month)));
  
        const profitData: number[] = [];
        const reservationsData: number[] = [];
  
        uniqueMonths.forEach(month => {
          const dataForMonth = chartData.find(data => data.month === month);
  
          profitData.push(dataForMonth?.profit/100 || 0);
          reservationsData.push(dataForMonth?.reservations || 0);
        });
  
        this.chartLabels = uniqueMonths;
        this.chartData = [
          { data: profitData, label: 'Profit (x100)', backgroundColor: '#037940' },
          { data: reservationsData, label: 'Reservations', backgroundColor: '#bc1823' }
        ];
      },
      error: (error) => {
        console.error('Error fetching analytics data', error);
      },
    });
  }
  
  exportToPDF(): void {
    const container = document.querySelector('.main-canvas') as HTMLElement;

    if (container instanceof HTMLElement) {
      html2canvas(container).then((canvas) => {
        const pdf = new jsPDF('l', 'mm', 'a4');
        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = pdf.internal.pageSize.getHeight();

        const title = 'Single Accommodation Chart';
        pdf.setFontSize(14);
        const titleWidth = pdf.getStringUnitWidth(title) * 14 / pdf.internal.scaleFactor;
        const titleX = (pdfWidth - titleWidth) / 2;
        const titleY = 10;

        pdf.text(title, titleX, titleY);

        pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 10, 30, pdfWidth - 20, pdfHeight - 40);

        pdf.save('single-analytics.pdf');
      });
    }
  }
  
}

