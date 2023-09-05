import {Component, OnInit} from '@angular/core';
import {
  ArcElement,
  BarController,
  BarElement,
  BubbleController,
  CategoryScale,
  Chart,
  Decimation,
  DoughnutController,
  Filler,
  Legend,
  LinearScale,
  LineController,
  LineElement,
  LogarithmicScale,
  PieController,
  PointElement,
  PolarAreaController,
  RadarController,
  RadialLinearScale,
  ScatterController,
  TimeScale,
  TimeSeriesScale,
  Title,
  Tooltip
} from 'chart.js';
import {Event} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {EventType} from '../../enums/event-type';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-events-top10',
  templateUrl: './events-top10.component.html',
  styleUrls: ['./events-top10.component.scss']
})

export class EventsTop10Component implements OnInit {


  events: Event[];

  eType: string;
  myChart: Chart = new Chart('myChart', null);
  rankings: number[];




  availableEventTypes: string[] = Object.values(EventType);


  constructor(private eventService: EventService, private snackbar: MatSnackBar) { }

  ngOnInit(): void {
    Chart.register(ArcElement,
      LineElement,
      BarElement,
      PointElement,
      BarController,
      BubbleController,
      DoughnutController,
      LineController,
      PieController,
      PolarAreaController,
      RadarController,
      ScatterController,
      CategoryScale,
      LinearScale,
      LogarithmicScale,
      RadialLinearScale,
      TimeScale,
      TimeSeriesScale,
      Decimation,
      Filler,
      Legend,
      Title,
      Tooltip);

    this.rankings = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    this.getTop10Events();

  }

  getTop10Events(): void {
    this.myChart.destroy();
    let eventType = this.eType;
    if (this.eType === undefined) {
      eventType = '';
    }

    this.eventService.getTop10Events(eventType).subscribe({
      next: data => {
        this.events = data;
        const lab: string[] = [];
        const ticketsSold: number[] = [];
        const bckColors: string[] = [];
        const brdrColors: string[] = [];

        const bckgColor =  [
          'rgba(255, 0, 0, 0.2)',
          'rgba(255, 154, 0, 0.2)',
          'rgba(208, 222, 33, 0.2)',
          'rgba(79, 220, 74, 0.2)',
          'rgba(63, 218, 216, 0.2)',
          'rgba(47, 201, 226, 0.2)',
          'rgba(28, 127, 238, 0.2)',
          'rgba(95, 21, 242, 0.2)',
          'rgba(186, 12, 248, 0.2)',
          'rgba(251, 7, 217, 0.2)'
        ];
        const brdrColor = [
          'rgba(255, 0, 0, 1)',
          'rgba(255, 154, 0, 1)',
          'rgba(208, 222, 33, 1)',
          'rgba(79, 220, 74, 1)',
          'rgba(63, 218, 216, 1)',
          'rgba(47, 201, 226, 1)',
          'rgba(28, 127, 238, 1)',
          'rgba(95, 21, 242, 1)',
          'rgba(186, 12, 248, 1)',
          'rgba(251, 7, 217, 1)'
        ];
        let i = 0;
        for (const val of data) {
          lab.push(val.name);
          ticketsSold.push(val.soldTickets);
          bckColors.push(bckgColor[i]);
          brdrColors.push(brdrColor[i]);
          i++;
        }
        this.myChart = new Chart('myChart', {
          type: 'bar',
          data: {
            labels: lab,
            datasets: [{
              label: '# of sold Tickets',
              data: ticketsSold,
              backgroundColor: bckColors,
              borderColor: brdrColors,
              borderWidth: 1
            }]
          },
          options: {
            plugins: {
              legend: {
                labels: {
                  boxWidth: 0
                }
              },
            },
            scales: {
              y: {
                beginAtZero: true,
                ticks: {
                  precision: 0
                }
              }
            }
          }
        });
      },
      error: err => {
        this.events = [];
        if (err.status === 0) {
          this.snackbar.open('The service is currently unavailable', 'Dismiss');
        }
      }
      }
    );
  }

  selectEventType(eventType: string) {
    this.eType = eventType;
    this.getTop10Events();
  }

  selectEventType2(eventType: string) {
    if (eventType === EventType.ball) {
      this.eType = 'BALL';
    } else if (eventType === EventType.concert) {
      this.eType = 'CONCERT';
    } else if (eventType === EventType.festival) {
      this.eType = 'FESTIVAL';
    } else if (eventType === EventType.opera) {
      this.eType = 'OPERA';
    }
    this.getTop10Events();
  }


}
