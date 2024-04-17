import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AnalyticsService } from '../analytics.service';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

@Component({
  selector: 'app-yearly-analytics',
  templateUrl: './yearly-analytics.component.html',
  styleUrls: ['./yearly-analytics.component.css'],
})
export class YearlyAnalyticsComponent implements OnInit {
  @Input() startDate: string = '';
  @Input() endDate: string = '';

  @ViewChild(BaseChartDirective, { static: true }) chart!: BaseChartDirective;
  @ViewChild('leftChart', { static: false }) leftChart!: BaseChartDirective;
  @ViewChild('rightChart', { static: false }) rightChart!: BaseChartDirective;


  chartType: ChartType;
  chartEarningsData: any[] = [];
  chartReservationsData: any[] = [];
  chartLabels: string[] = [];
  chartOptions: any = {
    responsive: true,
  };
  
  chartLegend = true;

  constructor(
    private analyticsService: AnalyticsService,
    private authService: AuthService
  ) {
    this.chartType = 'pie';
  }

  ngOnInit(): void {
    this.getAnalytics();
  }

  getAnalytics(): void {
    this.analyticsService.getAllAnalytics(this.startDate, this.endDate, this.authService.getUserID()).subscribe({
      next: (analytics) => {
        console.log(analytics);
  
        const profit: number[] = [];
        const reservations: number[] = [];
        const labels: string[] = [];
        const chartColors: any[] = ['#037940', '#bc1823', '#0077d8', '#fbbc04', '#f2994a'];
  
        analytics.forEach((analytic, index) => {
          profit.push(analytic.totalEarnings!);
          reservations.push(analytic.totalReservations!);
          labels.push(analytic.name!);
        });
  
        this.chartLabels = labels;
        this.chartEarningsData = [{ data: profit, label: 'Profit', backgroundColor: chartColors }];
        this.chartReservationsData = [{ data: reservations, label: 'Reservations', backgroundColor: chartColors }];
      },
      error: (error) => {
        console.error('Error fetching analytics data', error);
      },
    });
  }

  exportToPDF(): void {
    const container = document.querySelector('.main') as HTMLElement;
  
    if (container instanceof HTMLElement) {
      html2canvas(container).then((canvas) => {
        const pdf = new jsPDF('l', 'mm', 'a4');
        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = pdf.internal.pageSize.getHeight();
  
        const title = 'All Accommodations Analytics';
        pdf.setFontSize(14);
        const titleWidth = pdf.getStringUnitWidth(title) * 14 / pdf.internal.scaleFactor;
        const titleX = (pdfWidth - titleWidth) / 2;
        const titleY = 10;
  
        pdf.text(title, titleX, titleY);
  
        const chartContainers = document.querySelectorAll('.main-canvas') as NodeListOf<HTMLElement>;
  
        let yOffset = 30;
  
        chartContainers.forEach((chartContainer, index) => {
          if (chartContainer instanceof HTMLElement) {
            pdf.setFontSize(12);
  
            pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 10, yOffset + 10, pdfWidth - 20, pdfHeight - 40);
  
            yOffset += pdfHeight - 30;
          }
        });
  
        pdf.save('all-accommodations-analytics.pdf');
      });
    }
  }
  
}

