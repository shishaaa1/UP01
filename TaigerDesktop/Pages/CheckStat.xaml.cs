using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using TaigerDesktop.Connect;
using TaigerDesktop.Models;

namespace TaigerDesktop.Pages
{
    /// <summary>
    /// Логика взаимодействия для CheckStat.xaml
    /// </summary>
    public partial class CheckStat : Page
    {
        private readonly ApiContext _apiContext;
        public ObservableCollection<KpiCard> KpiCards { get; set; }
        private List<DailyStat> _statsData; // храним данные для графика
        public CheckStat()
        {
            InitializeComponent();
            _apiContext = new ApiContext();
            KpiCards = new ObservableCollection<KpiCard>();
            DataContext = this;
            Loaded += CheckStat_Loaded;
        }

        private async void CheckStat_Loaded(object sender, RoutedEventArgs e)
        {
            await LoadStatistics();
        }

        private async Task LoadStatistics()
        {
            try
            {
                var stats = await _apiContext.GetStatsLast30DaysAsync();
                _statsData = stats;

                // === KPI ===
                KpiCards.Clear();
                if (stats.Any())
                {
                    var today = stats.Last();
                    var yesterday = stats.Count >= 2 ? stats[^2] : null;
                    var last7 = stats.TakeLast(7);
                    var total = stats.Sum(s => s.NewUsers);

                    KpiCards.Add(new KpiCard { Label = "Сегодня", Value = today.NewUsers.ToString() });
                    KpiCards.Add(new KpiCard { Label = "Вчера", Value = yesterday?.NewUsers.ToString() ?? "—" });
                    KpiCards.Add(new KpiCard { Label = "Неделя", Value = last7.Sum(s => s.NewUsers).ToString() });
                    KpiCards.Add(new KpiCard { Label = "Всего", Value = total.ToString() });
                }
                else
                {
                    // Заглушка
                    KpiCards.Add(new KpiCard { Label = "Сегодня", Value = "0" });
                    KpiCards.Add(new KpiCard { Label = "Вчера", Value = "—" });
                    KpiCards.Add(new KpiCard { Label = "Неделя", Value = "0" });
                    KpiCards.Add(new KpiCard { Label = "Всего", Value = "0" });
                }

                DrawChart();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Не удалось загрузить статистику:\n{ex.Message}", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void DrawChart()
        {
            if (_statsData == null || !_statsData.Any()) return;

            GridCanvas.Children.Clear();
            PointsCanvas.Children.Clear();

            double canvasWidth = ChartCanvas.ActualWidth;
            double canvasHeight = ChartCanvas.ActualHeight;
            double margin = 40;
            double plotWidth = canvasWidth - 2 * margin;
            double plotHeight = canvasHeight - 2 * margin;

            if (plotWidth <= 0 || plotHeight <= 0) return;

            int max = _statsData.Max(s => s.NewUsers);
            if (max == 0) max = 1;

            double xStep = plotWidth / Math.Max(1, _statsData.Count - 1);
            double yScale = plotHeight / max;

            var points = new System.Collections.Generic.List<Point>();

            for (int i = 0; i < _statsData.Count; i++)
            {
                double x = margin + i * xStep;
                double y = canvasHeight - margin - (_statsData[i].NewUsers * yScale);
                points.Add(new Point(x, y));
            }

            // Линия
            if (points.Count > 1)
            {
                var geometry = new StreamGeometry();
                using (var ctx = geometry.Open())
                {
                    ctx.BeginFigure(points[0], false, false);
                    ctx.PolyLineTo(points.Skip(1).ToArray(), true, false);
                }
                ChartPath.Data = geometry;
            }
            else
            {
                ChartPath.Data = null;
            }

            // Точки
            PointsCanvas.Children.Clear();
            foreach (var p in points)
            {
                var ellipse = new Ellipse
                {
                    Width = 6,
                    Height = 6,
                    Fill = (Brush)FindResource("PrimaryBrush")
                };
                Canvas.SetLeft(ellipse, p.X - 3);
                Canvas.SetTop(ellipse, p.Y - 3);
                PointsCanvas.Children.Add(ellipse);
            }
        }

        private void ChartCanvas_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            DrawChart(); // перерисовываем при изменении размера
        }

        private void ChartCanvas_MouseMove(object sender, MouseEventArgs e)
        {
            if (_statsData == null || !_statsData.Any()) return;

            var pos = e.GetPosition(ChartCanvas);
            double canvasWidth = ChartCanvas.ActualWidth;
            double margin = 40;
            double plotWidth = canvasWidth - 2 * margin;

            if (pos.X < margin || pos.X > canvasWidth - margin) return;

            int index = (int)Math.Round((pos.X - margin) / (plotWidth / Math.Max(1, _statsData.Count - 1)));
            index = Math.Max(0, Math.Min(_statsData.Count - 1, index));

            var stat = _statsData[index];
            TooltipText.Text = $"{stat.Date:dd MMM}\nНовые: {stat.NewUsers}";
            Canvas.SetLeft(TooltipBorder, pos.X + 10);
            Canvas.SetTop(TooltipBorder, pos.Y - 30);
            TooltipBorder.Visibility = Visibility.Visible;
        }
    }
}

